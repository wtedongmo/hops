$(function(){

    if($('#table-bank-js').length) {
        var table = $('#table-bank-js').DataTable({
            "pageLength": 10,
            'searching': false,
            "scrollX": true,
            "sDom": 'Rfrtilp',
            "infoCallback": function( settings, start, end, max, total, pre ) {
                return "<label class='control-label'>Total :</label> <strong class='tc_num'>"+total+"</strong>";
            }

        });
    }



    if($('#table-notices-js').length) {
        var table = $('#table-notices-js').DataTable({
            "pageLength": 10,
            'searching': false,
            "scrollX": true,
            "sDom": 'Rfrtilp',
            "infoCallback": function( settings, start, end, max, total, pre ) {
                return "<label class='control-label'>Total :</label> <strong class='tc_num'>"+total+"</strong>";
            }

        });


        // Add event listener for opening and closing details
        $('#table-notices-js tbody').on('click', 'td.details-control', function () {
            var tr = $(this).closest('tr');
            var row = table.row( tr );

            var parentId = $(this).data('parent-row');
            console.log(parentId);
            var apDetails = $(".ap-details-"+parentId).html();
            var amountDetails = $(".amount-details-"+parentId).html();

            if ( row.child.isShown() ) {
                // This row is already open - close it
                row.child.hide();
                tr.removeClass('shown');
                tr.find('i').removeClass('fa-minus').addClass('fa-plus');

            }
            else {
                // Open this row
                row.child(apDetails+amountDetails).show();
                tr.addClass('shown');
                tr.find('i').removeClass('fa-plus').addClass('fa-minus');
            }
        });
    }


    $(".locales").click(function () {
        var selectedOption = $(this).data('locale').trim();
        if (selectedOption != ''){
            window.location.replace('/epayment-administration/lang?lang=' + selectedOption);
        }
    });
});