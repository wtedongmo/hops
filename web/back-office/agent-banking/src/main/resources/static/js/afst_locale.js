
$(function(){
    $(".locales").click(function () {
        var selectedOption = $(this).data('locale').trim();
        if (selectedOption != ''){
            window.location.replace('hops-admin/?lang=' + selectedOption);
        }
    });
}
