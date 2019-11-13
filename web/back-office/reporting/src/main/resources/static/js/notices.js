$(function(){

    if($('#table-notices-js').length) {
        $('#table-notices-js').DataTable({
            "pageLength": 10,
            "bLengthChange": false,

        });
    }
});