$(function(){
    $('a.transaction').click(function(){
        window.open(this.href);
        return false;
    });
});