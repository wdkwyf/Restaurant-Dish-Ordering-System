/**
 * Created by LLLLLyj on 2016/1/13.
 */
var editClicked = false;
var addClicked  = false;
var manager_id = getCookie("manager_id");

$.ajax({
    type: "GET",
    url: "http://10.60.44.189:3000/alldish/" + manager_id,
    success: function(data){
        $("#menu-table").bootstrapTable("load", data);
    }
});

function initTable(){
    $.ajax({
        type: "GET",
        url: "http://10.60.44.189:3000/alldish/" + manager_id,
        success: function(data){
            $("#menu-table").bootstrapTable("load", data);
        }
    });
}

function editSend(json_request){
    console.log(json_request);
    $.ajax({
        type: "POST",
        url: "http://10.60.44.189:3000/console/dish/" + manager_id,
        data: {edit:json_request},
        ContentType: "application/json"
    });
}

function deleteSend(json_request){
    $.ajax({
        type: "DELETE",
        crossDomain: true,
        url: "http://10.60.44.189:3000/console/dish/" + manager_id,
        data: {dishId:json_request},
        ContentType: "application/json"
    });
}

function addSend(json_request){
    $.ajax({
        type: "POST",
        url: "http://10.60.44.189:3000/newdish/" + manager_id,
        data: {dish:json_request},
        ContentType: "application/json"
    });
}

function addSetMealSend(setmeal, dishes){
    $.ajax({
        type: "POST",
        url: "http://10.60.44.189:3000/newsetmeal/" + manager_id,
        data: {setmeal:setmeal, has:dishes},
        ContentType: "application/json"
    });
}

function deleteSetMealSend(id){
    $.ajax({
        type: "POST",
        url: "http://10.60.44.189:3000/newsetmel/0000",
        data: {id:id},
        ContentType: "application/json"
    });
}

function editItem(){
    var trlist = $("#menu-table>tbody>tr");
    var i;
    if(editClicked){
        $("#edit-btn").text("修改");
        editClicked = false;

        var request = [];
        for (i = 0; i < trlist.length; i++) {
            if (trlist[i].hasAttribute("class")) {
                var tds = trlist[i].getElementsByTagName("td");
                var edit_id   = tds[1].innerHTML;
                var edit_type = tds[2].innerHTML;
                var edit_name = tds[3].innerHTML;
                var edit_spend= tds[4].innerHTML;
                var edit_wait = tds[5].innerHTML;
                request.push({
                    dishid: edit_id,
                    info:{
                        name: edit_name,
                        waiting_time: edit_wait,
                        spend: edit_spend,
                        dish_type: edit_type,
                    }
                });

            } else {
                trlist[i].setAttribute("contenteditable", "false");
            }
        }
        editSend(request);
    }else {
        $("#edit-btn").text("确认修改");
        editClicked = true;
        for (i = 0; i < trlist.length; i++) {
            if (trlist[i].hasAttribute("class")) {
                trlist[i].setAttribute("contenteditable", "true");
            } else {
                trlist[i].setAttribute("contenteditable", "false");
            }
        }

    }
}

function deleteItem(){

    var trlist = $("#menu-table>tbody>tr");
    for(var i = 1; i < trlist.length ; i++) {
        var item = $("#menu-table tr:eq(" + i + ")");
        if(item.attr("class")) {
            id = $("#menu-table tr:eq(" + i + ") td:nth-child(2)").html();
            item.remove();
            deleteSend(id);
        }
    }

}

function addItem(){
    var total_len = $("#menu-table>tbody>tr").length;
    var add_index = total_len + 1;
    console.log(add_index);
    if(addClicked) {
        var tr           = $("#menu-table>tbody>tr")[total_len-1];
        var tds          = tr.getElementsByTagName("td");
        var add_type = tds[2].innerHTML;
        var add_name = tds[3].innerHTML;
        var add_spend= tds[4].innerHTML;
        var add_wait = tds[5].innerHTML;

        var json_request = [
            {
                "name":add_name,
                "tag":0,
                "spend":add_spend,
                "waiting_time": add_wait,
                "dish_type": add_type
            }
        ];
        console.log(json_request);

        addSend(json_request);

        addClicked = false;
        $("#add-btn").html("增加");
    }else{
        $('<tr data-index="'+ add_index
            +'" contenteditable="true"><td class="bs-checkbox"><input data-index="'
            +add_index+'" name="toolbar1" type="checkbox"></td><td style=""></td><td style="">' +
            '</td><td style=""></td><td style=""></td><td style=""></td></tr>"').insertAfter("#menu-table tr:eq("+ total_len +")");
        addClicked = true;
        $("#add-btn").html("确认增加");
    }
}

function viewSetMeal(){
    var selections = $("#set-meal-table").bootstrapTable("getSelections");
    for(i = 0;i < selections.length; i++){
        var set_meal_id= selections[i].id;
        $.ajax({
            type: "GET",
            url: "http://10.60.44.189:3000/setmealinfo/" + manager_id + "/" + set_meal_id,
            success: function(data){
                console.log(data);
                $("#set-meal-has-dish-table").bootstrapTable("load", data);
            }
        });
    }
}


function setMealDelete(){
    var table = $("#set-meal-table");
    var selections = table.bootstrapTable("getSelections");

    for(i = 0; i < selections.length; i++){
        var s = selections[i];
        var del_id = [s.id];
        table.bootstrapTable("remove", {field:'id', values: del_id});
        //deleteSetMealSend(del_id[0]);
    }

}

var setMealAddClicked = false;
function setMealAdd(){
    var hasDishTable = $("#set-meal-has-dish-table");
    var setMealTable = $("#set-meal-table");
    if(setMealAddClicked){
        setMealAddClicked = false;
        var hasDish = hasDishTable.bootstrapTable("getSelections");
        var hasDishJson = [];
        for(d = 0; d < hasDish.length; d++){
            var dd = hasDish[d];
            hasDishJson.push({dishid: dd.id, num: dd.number});
        }
        console.log(hasDishJson);
        var tr = $("#set-meal-table>tbody>tr");
        var tds       = tr[tr.length-1].getElementsByTagName("td");
        var add_name  = tds[2].innerHTML;
        var add_price = tds[3].innerHTML;

        var setmeal = {set_name:add_name, spend: add_price};
        addSetMealSend(setmeal, hasDishJson);

        $("#set-meal-add-btn").html("增加");
    }else{
        setMealAddClicked = true;
        $.ajax({
            type: "GET",
            url: "http://10.60.44.189:3000/alldish/" + manager_id,
            success: function(data){
                $("#set-meal-has-dish-table").bootstrapTable("load", data);
            }
        });

        setMealTable.bootstrapTable("append", {});
        var trlist = $("#set-meal-table>tbody>tr");
        trlist[trlist.length-1].setAttribute("contenteditable", "true");

        $("#set-meal-add-btn").html("确认增加");
    }
}







