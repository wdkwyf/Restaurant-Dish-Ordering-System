/**
 * Created by LLLLLyj on 2016/1/15.
 */
var manager_id = getCookie("manager_id");

loadCallTable();

function loadCallTable(){
    $.ajax({
        type: "GET",
        url: "http://10.60.44.189:3000/callinfo/" + manager_id,
        success: function(data){
            console.log(data);
            $("#call-table").bootstrapTable("load", data);
        }
    });
}