/*!
 * Start Bootstrap - SB Admin 2 v3.3.7+1 (http://startbootstrap.com/template-overviews/sb-admin-2)
 * Copyright 2013-2016 Start Bootstrap
 * Licensed under MIT (https://github.com/BlackrockDigital/startbootstrap/blob/gh-pages/LICENSE)
 */

//Loads the correct sidebar on window load,
//collapses the sidebar on window resize.
// Sets the min-height of #page-wrapper to window size
$(function() {

    var url = window.location;
    var element = $('.sidebar-menu a').filter(function() {
        return this.href == url;
    }).parent().addClass('active').parent();

    while (true) {
        if (element.is('ul')) {
            element = element.parent().addClass('active').parent();
        } else {
            break;
        }
    }
});
