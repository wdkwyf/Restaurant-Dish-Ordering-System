/**
 * Created by LLLLLyj on 2016/1/15.
 */

var manager_id = getCookie("manager_id");

refreshTableStatus();

function refreshTableStatus(){
    $.ajax({
        type: "GET",
        url: "http://10.60.44.189:3000/tableinfo/" + manager_id,
        success: function(data){
            console.log(data);
            for(i = 0; i < data.length; i++){
                var tid = i + 1;
                if(data[i].status == "0"){
                    $("#table" + tid).attr("src", "img/table.png");
                }else{
                    $("#table" + tid).attr("src", "img/red_table.png");
                }
            }
        }
    });
}