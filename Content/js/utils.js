add_event(window, 'load', init_event, false);

/* Lo que se realizara cuando la pagina este cargada */
function init_event() {
    take_lose_focus_text("user");
    take_lose_focus_text("keywords");
    take_lose_focus_text("labels");
    
    var ob=document.getElementById('submit');
    add_event(ob, 'click', send_data, false);
}

function send_data(e) {
/* Antes de enviar la informacion al servidor vamos a comprobar que esten cubiertos de forma correcta los campos */
    var user  = document.getElementById("user");
    var keywords  = document.getElementById("keywords");
    var label = document.getElementById("labels");
    
    /* Vaciamos si hay antes algo de informacion */
    delete_gallery();
    delete_info();
    hide_answer();
    
    /* Miramos si podemos enviar el formulario */
    if (!((user.value!='' && keywords.value=='') || (user.value=='' && label.value=='' && keywords.value!=''))){
        alert('Search allowed:\n\tUsername\n\tUsername & Labels\n\tKeywords');
        return false;/* Con IE asi evito que se envie el formulario */        
    }

    /* Preparamos los datos que se enviaran */
    if (user.value != '') {
        if (label.value != '')/* Se envia por usuario y por etiqueta */
            search_data("service=buscarUsuarioEtiquetas&username=" + user.value +"&tags=" + label.value);
        else/* Se envia solo por usuario */
            search_data("service=buscarUltimasUsuario&username=" + user.value);
    } else/* Se envia por palabras clave */
        search_data("service=buscarPalabrasClave&keywords=" + keywords.value);
    
}

/* funcion que realiza la peticion de busqueda */
function search_data(data) {
    var info = document.getElementById('info');
    var loading = document.createElement('img');
    loading.setAttribute('src', "./images/loading.gif");
    loading.setAttribute('alt', "Loading");
    loading.setAttribute('class', "image");
    loading.setAttribute('id', "Loading");
    loading.setAttribute('width', "125");
    loading.setAttribute('height', "125");
    
    info.appendChild(loading);

    cargador = new net.load_data('/cgi-bin/service.py',
                                 check_answer, //Funcion que procesara la respuesta
                                'POST',//Metodo de envio
                                 data, //Datos que se enviaran en la peticion
                                 'application/x-www-form-urlencoded'); //Indicamos que es un formulario con datos POST


}

/* funcion que recibe la respuesta del servidor y la comprueba */
function check_answer() {
    /* Comprobamos que no nos envie una respuesta vacia */
    if (this.responseText == ''){
        report_error('Failed to get data');
        return;
    }
    
    /* Convertimos el mensaje JSON en un objeto DOM */
    var image = eval('(' + this.req.responseText + ')');
    if (image == '') {/* Miramos si la busqueda tiene alguna imagen */
        report_error('Images not found');
        return;
    }
    
    /* Miramos que no se tenga ningun error */
    if (image.result == 'error') {
        /* Si no insertamos usuario */
        if(image.data.reason.indexOf('Missing parameter: username') != -1)
            repert_error("Must include the user's account");
        
        /* Si insertamos un usuario que no existe*/
        if(image.data.reason.indexOf('Unknown user') != -1  || image.data.reason.indexOf('Unable to find user with email') != -1)
            report_error('Not found any user with the specified name');
        
        /* No insertamos ninguna palabra clave */
        if (image.data.reason.indexOf('Missing parameter: keywords') != -1)
            report_error('Must introduce keywords');
        
        /* Si insertamos etiquetas pero no decimos en que cuenta de usuario buscar */
        if (image.data.reason.indexOf('Missing parameter: tags') != -1)
            report_error('Must introduce username');
        return;
    }
    
    /* Mostramos donde vamos a cargar la respuesta */
    var answer = document.getElementById('answer');
    answer.style.display = 'inline'; 
    
    delete_gallery();/* Vaciamos por si hab’a alguna de antes */
    var gallery = document.getElementById('gallery');
    
    /* A–adiendo las imagenes que nos indico el servidor */
    for (var i = 0; image[i] != null; i++)
        gallery.appendChild(create_image_html(image[i]));

    /* Eliminamos el waitting */
    delete_info();

}

/* Crea en HTML la imagen para a–adirla a la pagina */
function create_image_html(data) {
    var href;
    var image;
    
    href = document.createElement('a');
    href.setAttribute('href', data.url);
    
    picture = document.createElement('img');
    picture.setAttribute('src',data.thumbnail_src);
    picture.setAttribute('alt', data.title);
    picture.setAttribute('title', data.title);
    picture.setAttribute('class', 'image');
    
    href.appendChild(picture);
    
    return href;
}

/* Borra lo que hay en el campo de informacion */
function delete_info() {
    var info=document.getElementById('info');
    while (info.hasChildNodes())
        info.removeChild(info.lastChild);
}

/* Elimina las fotos de la gallery */
function delete_gallery() {
    var info=document.getElementById('gallery');
    while (info.hasChildNodes())
        info.removeChild(info.lastChild);
}

/* oculta la parte donde se muestra la galeria */
function hide_answer() {
    var info=document.getElementById('answer');
    info.style.display = 'none';
}

/* Informa de un mensaje de error */
function report_error(message) {
    var element = document.getElementById('info');
    while (element.hasChildNodes())
        element.removeChild(element.lastChild);

    /* Creo el elemento h2 */
    var h2=document.createElement('h2');
    var text=document.createTextNode(message);
    h2.appendChild(text);
    element.appendChild(text);

}


/* Se a–aden los efectos cuando un input tiene o no el foco */
function take_lose_focus_text(name) {
    var ob = document.getElementById(name);
    add_event(ob, 'focus', take_focus_text, false);
    add_event(ob, 'blur',  lose_focus_text, false);
    add_event(ob, 'keypress', catch_enter_text, false);
}

function take_focus_text(e) {
    if (window.event) {
        window.event.srcElement.style.fontWeight='bold';
    } else
        if (e) {
            e.target.style.fontWeight='bold';
        }
}

function lose_focus_text(e) {
    if (window.event) {
        window.event.srcElement.style.fontWeight='normal';
    } else
        if (e) {
            e.target.style.fontWeight='normal';
        }  
}

/* Anadimos lo de presionar enter para que se envie el formulario */
function catch_enter_text(e) {
    if (window.event) {
        var key=window.event.keyCode;
        if (key==13)
            send_data();
    } else
        if (e) {
        var key=e.which;
        if(key==13)
            send_data();
        }
}

function add_event(element, namevent, func, capture)
{
  if (element.attachEvent)/* Si es IE */
  {
    element.attachEvent('on'+namevent,func);
    return true;
  }
  else  
    if (element.addEventListener)/* Si cumple el estandar W3C */
    {
      element.addEventListener(namevent,func,capture);
      return true;
    }
    else /* Otros */
      return false;
}