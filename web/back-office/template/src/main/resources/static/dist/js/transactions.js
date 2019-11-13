$(function () {
    $('a.transaction').click(function () {
        window.open(this.href);
        return false;
    });
});

$(function () {
    $('#feePayer').click(function () {
        var temp = $('#feePayer option:selected').text();
        var payerType = $('#payerType').val(temp)
    })
});

$(function () {
    var search = $('#search').val();

        if(search=='false'){
            var feePayerValue = $('#feePayerId').val();
            $('#payerName').val(feePayerValue);
            var feeReceiverValue =$('#feeReceiverId').val();
            $('#feeReceiverType').val(feeReceiverValue);
        }
});

$(function () {
    var temp = $('#feePayer option:first-child').text();
    $('#payerType').val(temp)
});

$(function () {
    $('#feeReceiverType').click(function () {
        var type = $('#feeReceiverType option:selected').text();
        $('#receiverType').val(type)
    })
});

/*$(function () {
    var receiverType = $('#feeReceiverType option:first-child').text();
    $('#receiverType').val(receiverType)
});*/

$(function () {
    var date = new Date();
    var nextDay = new Date(date);
    nextDay.setDate(date.getDate() + 1);
    var search = $('#search').val();
    if (search == 'false') {
        var dtFrom = $('#fmDate').val();
        var dtTo = $('#toDate').val();
        var fd = new Date(dtFrom);
        var td = new Date(dtTo);
        document.querySelector("#fromDatePicker").valueAsDate = fd;
        document.querySelector("#toDatePicker").valueAsDate = td;
        $('#fromDatePicker').change(function () {
            var fromDate = $('#fromDatePicker').val();
            if(fromDate == ""){
                $('#fromDatePicker').after('<div style="color: red">Please select the date</div>');
                document.querySelector("#fromDatePicker").valueAsDate = fd;
            }
        });
        $('#toDatePicker').change(function () {
            var toDate = $('#toDatePicker').val();
            if(toDate == ""){
                $('#toDatePicker').after('<div style="color: red">Please select the date</div>');
                document.querySelector("#toDatePicker").valueAsDate = td;
            }
        });
        var statusVal = $('#setStatus').val();
        $('#setStausValue').val(statusVal);
        var inBound = $('#inBoundId').val();
        $('#inBound').val(inBound);
        var outBoundVal = $('#outBoundId').val();
        $('#outBound').val(outBoundVal);

    } else {
        document.querySelector("#fromDatePicker").valueAsDate = date;
        document.querySelector("#toDatePicker").valueAsDate = nextDay;
        $('#fromDatePicker').change(function () {
            var fromDate = $('#fromDatePicker').val();
            if(fromDate == ""){
                $('#fromDatePicker').after('<div style="color: red">Please select the date</div>');
                document.querySelector("#fromDatePicker").valueAsDate = date;
            }
        });
        $('#toDatePicker').change(function () {
            var toDate = $('#toDatePicker').val();
            if(toDate == ""){
                $('#toDatePicker').after('<div style="color: red">Please select the date</div>');
                document.querySelector("#toDatePicker").valueAsDate = nextDay;
            }
        })
    }
});
