var app = require('./app')
var mysql      = require('mysql');
var bodyParser = require('body-parser');
var rt = require('./gettime');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
  extended: true
}));

app.post('/waitercall/',function(req,res){
	var tableID = req.body.tableId;
	console.log("--------");
	console.log(tableID);
	console.log("waiter call");
	console.log("--------")
	var callTime = rt.getDateTime();
	var status = 0;
	var connection = mysql.createConnection({
	    host     : '121.42.210.194',
	    user     : 'root',
	    password : 'lyj5654582',
	    database : 'restaurant_db'
	 });	
	connection.connect();
	connection.query("select id,called_times from call_message where dining_table_id = ?",[tableID],function(err,rows,fields){
		if (err) throw err;
		if(rows.length != 0){
			var new_call_times = rows[0].called_times + 1;
			var updateSQL = "UPDATE call_message SET called_times = ?,status = 1 where dining_table_id = ?";
			connection.query(updateSQL,[new_call_times,tableID],function(err,rows,fields){
				if (err) throw err;
				res.send(JSON.stringify({"status":1}));
				connection.end();
			});
		}else{
			connection.query("select id as num from `call_message`",function(err,rows,fields){
				if (err) throw err;
				var idList = [];
				var newId = 0;
				if (rows.length != 0){
					for(var i = 0; i<rows.length;i++){
						idList.push(rows[i].num);
					}
					newId = Math.max.apply(Math,idList) + 1;
				}else{
					newId = 1;
				}
				var insertItem = {id:newId,status:1,called_times:1,dining_table_id:tableID,manager_id:"0000"};
				connection.query("insert into call_message set ?",insertItem,function(err,rows,fields){
					if (err) throw err;
					res.send(JSON.stringify({"status":1}));
					connection.end();
				});
			});
		}
	});
});

app.post('/newcustomer/',function(req,res){
	var customerCnt = req.body.customerCount;
	var tableID = req.body.tableId;
	res.setHeader('Content-Type',"application/json;charset=utf-8");
	res.setHeader("Access-Control-Allow-Origin","*");
	res.setHeader("Access-Control-Allow-Methods","POST,GET,DELETE,OPTION");
	console.log("----------");
	console.log("-new customer-");
	console.log(req.body);
	console.log("----------");
	var connection = mysql.createConnection({
	    host     : '121.42.210.194',
	    user     : 'root',
	    password : 'lyj5654582',
	    database : 'restaurant_db'
	 });
	connection.connect();
	connection.query("update `dining_table` set `status` = 1 where `id` = ? and `manager_id` = '0000'",[tableID],function(err,rows,fields){
		if (err) throw err;
		var insertData = {tableId:tableID,customerCount:customerCnt,Serial:rt.getDateTime()};
		connection.query("insert into customerCount set ?",insertData,function(err,rows,fields){
			if (err) throw err;
			res.send("Ok");
			connection.end();
		});
	});
});	

app.post('/waitercall/response/',function(req,res){
	var tableID = req.body.tableId;
	res.setHeader('Content-Type',"application/json;charset=utf-8");
	res.setHeader("Access-Control-Allow-Origin","*");
	res.setHeader("Access-Control-Allow-Methods","POST,GET,DELETE,OPTION");
	console.log("----------");
	console.log("-waiter call response-");
	console.log(req.body);
	console.log("----------");
	var connection = mysql.createConnection({
	    host     : '121.42.210.194',
	    user     : 'root',
	    password : 'lyj5654582',
	    database : 'restaurant_db'
	 });
	connection.connect();
	connection.query("update call_message set status = 0 where dining_table_id = ? and manager_id = '0000'",[tableID],function(err,rows,fields){
		console.log(tableID);
		if (err) throw err;
		res.send(JSON.stringify({"status":0}));
		connection.end();
	});
});