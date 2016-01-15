/**
 * Created by LLLLLyj on 2016/1/13.
 */

setCookie("manager_id", "", -1);
$(function() {

        $("#login-submit-button").click(function () {
            $.ajax({
                type: "POST",
                url: "http://10.60.44.189:3000/manager/login",
                data: {username: $("#login-user-name").val(), password: $("#login-password").val()},
                ContentType: "application/json",
                success: function (data) {
                    if (data.status == "0") {
                        location.href = "index.html";
                        setCookie("manager_id", data.manager_id, 1);
                    }
                    else if (data.status == "1") {
                        $("#login-notice").text("Wrong pass word");
                    }
                    else {
                        $("#login-notice").text("Invalid username");
                    }
                },
                error: function (data) {
                    $("#login-notice").html("check your internet connection");
                }
            });
        });

        $("#login-heading").click(function(){
            location.href = "index.html";
        });
    }
)