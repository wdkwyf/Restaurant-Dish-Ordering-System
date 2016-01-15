/**
 * Created by LLLLLyj on 2016/1/15.
 */
var manager_id = getCookie("manager_id");

loadStatics();

function loadStatics(){
    $.ajax({
        type: "GET",
        url: "http://10.60.44.189:3000/countinfo/" + manager_id,
        success: function(data){
            console.log(data);
            $("#order-count").html(data.count_order);
            $("#call-count").html(data.count_call);
            $("#dining-table-count").html(18 - data.count_table);
            $("#daily-amount").html(data.sum_order);
        }
    });
}