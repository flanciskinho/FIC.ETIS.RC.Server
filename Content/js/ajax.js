var net = new Object();

/* Estos son los posibles estados que deber’a tener la conexi—n */
net.READY_STATE_UNINITIALIZED=0;
net.READY_STATE_LOADING=1;
net.READY_STATE_LOADED=2;
net.READY_STATE_INTERACTIVE=3;
net.READY_STATE_COMPLETE=4;


net.load_data = function(url, action, method, parameter, content_type) {
    this.url = url;  
    this.req = null; /* Objeto que realizara la peticion */
    this.on_load = action;  /* lo que se har‡ cuando se tenga la respuesta */
    this.send_request(url, method, parameter, content_type); /* el que realiza la respuesta */
    this.canceled(); /* Funci—n para cancelar la peticion */
    this.cancel= false; /* Para indicar que la peticion ha sido cancelada */
}

/* Aqui los metodos que tiene la clase */
net.load_data.prototype = {
    /* para cancelar la peticion */
    canceled: function() {
        this.cancel = true;
    },

    /* quien envia la peticion */
    send_request: function(url, method, parameter, content_type) {
        /* guardamos el objeto que realizara el envio */
        if(window.XMLHttpRequest)
            this.req = new XMLHttpRequest();  //Version estandar
        else if(window.ActiveXObject) {//Versiones no estandares
            try {            
                this.req = new ActiveXObject("Msxml2.XMLHTTP");
            }
            catch(e) {            
                this.req = new ActiveXObject("Microsoft.XMLHTTP");            
            }
        }

        if(this.req) {
            /* realizamos la peticion */
            try {
                var loader = this;
                this.req.onreadystatechange = function() {
                    loader.onReadyState.call(loader);
                }

                this.req.open(method, url, true);
                
                if(content_type)
                    this.req.setRequestHeader("Content-Type", content_type);
                   
                this.req.send(parameter);
            }
            catch(err) {
                alert('There was an error getting data(prototype)\n');
            }
        }
        else
            alert('Error: Your browser does not support AJAX');

    },

    /* quien recibe la respuesta */
    onReadyState: function() {
        /* Si esta anulado pasamos de la respuesta que nos ha devuelto el servidor */
        if(!this.cancel) {
            var req = this.req;
            var ready = req.readyState;
            if(ready == net.READY_STATE_COMPLETE) {
                var httpStatus = req.status;

                /* Procesamos la respuesta si la recibimos de la forma correcta */
                if(httpStatus == 200 || httpStatus == 0)
                    this.on_load.call(this);
                else
                    alert('There was an error getting data(onReadyState)\n');
            }
        }
    },

}
