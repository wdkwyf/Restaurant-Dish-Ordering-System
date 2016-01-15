/**
 * Created by LLLLLyj on 2016/1/15.
 */
var manager_id = getCookie("manager_id");
loadOrderTable();

function loadOrderTable(){
    $.ajax({
        type: "GET",
        url: "http://10.60.44.189:3000/orderinfo/" + manager_id,
        success: function(data){
            for(i = 0; i < data.length; i++){
                switch (data[i].status){
                    case 0:
                        data[i].status = "未完成";
                        break;
                    case 1:
                        data[i].status = "已完成";
                        break;
                    case 2:
                        data[i].status = "已结账";
                        break;
                    default:
                        break;
                }
            }
            $("#order-table").bootstrapTable("load", data);
        }
    });
}

function loadDishTable(){
    var select = $("#order-table").bootstrapTable("getSelections")[0];
    var id = select.id;
    console.log(id);

    $.ajax({
        type: "GET",
        url: "http://10.60.44.189:3000/dishinfo/" + id,
        success: function(data){
            console.log(data);
            $("#dish-table").bootstrapTable("load", data);
        }
    });

}