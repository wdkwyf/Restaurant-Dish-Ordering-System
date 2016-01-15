var app = require('./app')
var mysql      = require('mysql');
var bodyParser = require('body-parser');
var rt = require('./gettime');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
  extended: true
}));
var constructID = require('./constructID');

app.post('/manager/login/',function(req,res){
	var username = req.body.username;
	var passwd = req.body.password;
	console.log(passwd);
	var connection = mysql.createConnection({
    host     : '121.42.210.194',
    user     : 'root',
    password : 'lyj5654582',
    database : 'restaurant_db'
  });	

	connection.connect();
	var hasManager = "Select password as pd,id as userId from manager where username =?";
	connection.query(hasManager,[username],function(err,rows,fields){
		res.setHeader('Content-Type',"application/json;charset=utf-8");
		res.setHeader("Access-Control-Allow-Origin","*");
		res.setHeader("Access-Control-Allow-Methods","POST");
		if (err) throw err;
		if (rows.length != 0){
			var pd = rows[0].pd;
			var userID = rows[0].userId;
			if (pd != passwd){
				console.log("Wrong pd");
				res.send("1");
			}else{
				console.log("log in OK!");
				res.send(JSON.stringify({status:"0",manager_id:userID}));
			}
		}else{
			console.log("No User");
			res.send("2");
		}
	});
	connection.end();
});
