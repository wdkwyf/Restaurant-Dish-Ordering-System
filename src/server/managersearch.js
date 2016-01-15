var app = require('./app')
var mysql      = require('mysql');
var bodyParser = require('body-parser');
var rt = require('./gettime');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
  extended: true
}));
var constructID = require('./constructID');

app.get('/orderinfo/:manager_id',function(req,res){
	var managerID = req.params.manager_id;
	res.setHeader('Content-Type',"application/json;charset=utf-8");
	res.setHeader("Access-Control-Allow-Origin","*");
	res.setHeader("Access-Control-Allow-Methods","POST,GET,DELETE,OPTION");
	console.log("----------");
	console.log("get order info");
	console.log(managerID);
	console.log("----------");
	var connection = mysql.createConnection({
	    host     : '121.42.210.194',
	    user     : 'root',
	    password : 'lyj5654582',
	    database : 'restaurant_db'
	 });
	connection.connect();
	connection.query("select id,status,spend,dining_table_id from `order` where manager_id = ?",[managerID],function(err,rows,fields){
		if (err) throw err;
		res.send(JSON.stringify(rows));
		connection.end;
	});
});

app.get('/dishinfo/:order_id',function(req,res){
	var orderID = req.params.order_id;
	res.setHeader('Content-Type',"application/json;charset=utf-8");
	res.setHeader("Access-Control-Allow-Origin","*");
	res.setHeader("Access-Control-Allow-Methods","POST,GET,DELETE,OPTION");
	console.log("----------");
	console.log("get dish info");
	console.log(orderID);
	console.log("----------");
	var connection = mysql.createConnection({
	    host     : '121.42.210.194',
	    user     : 'root',
	    password : 'lyj5654582',
	    database : 'restaurant_db'
	 });
	connection.connect();
	connection.query("select * from `dish` where id in (select dish_id from `order_has_dish` where order_id = ?)",[orderID],function(err,rows,fields){
		if (err) throw err;
		if(rows.length != 0){
			res.send(JSON.stringify(rows));
		}else{
			res.send(JSON.stringify([]));
		}
	});
});

app.get('/callinfo/:manager_id',function(req,res){
	var managerID = req.params.manager_id;
	res.setHeader('Content-Type',"application/json;charset=utf-8");
	res.setHeader("Access-Control-Allow-Origin","*");
	res.setHeader("Access-Control-Allow-Methods","POST,GET,DELETE,OPTION");
	console.log("----------");
	console.log("get call info");
	console.log(managerID);
	console.log("----------");
	var connection = mysql.createConnection({
	    host     : '121.42.210.194',
	    user     : 'root',
	    password : 'lyj5654582',
	    database : 'restaurant_db'
	 });
	connection.connect();
	connection.query("select id,status,called_times,dining_table_id from `call_message` where manager_id = ?",[managerID],function(err,rows,fields){
		if (err) throw err;
		res.send(rows);
		connection.end();
	});
});

app.get('/tableinfo/:manager_id',function(req,res){
	var managerID = req.params.manager_id;
	res.setHeader('Content-Type',"application/json;charset=utf-8");
	res.setHeader("Access-Control-Allow-Origin","*");
	res.setHeader("Access-Control-Allow-Methods","POST,GET,DELETE,OPTION");
	console.log("----------");
	console.log("get table info");
	console.log(managerID);
	console.log("----------");
	var connection = mysql.createConnection({
	    host     : '121.42.210.194',
	    user     : 'root',
	    password : 'lyj5654582',
	    database : 'restaurant_db'
	 });
	connection.connect();
	connection.query("select id,number_of_seat,status from dining_table where manager_id = ?",[managerID],function(err,rows,fields){
		if (err) throw err;
		res.send(rows);
		connection.end();
	});
});

app.get('/setmealInfo/:manager_id/:setmealId',function(req,res){
	var setmealID = req.params.setmealId;
	res.setHeader('Content-Type',"application/json;charset=utf-8");
	res.setHeader("Access-Control-Allow-Origin","*");
	res.setHeader("Access-Control-Allow-Methods","POST,GET,DELETE,OPTION");
	console.log("----------");
	console.log("get setmeal info");
	console.log(setmealID);
	console.log("----------");
	var connection = mysql.createConnection({
	    host     : '121.42.210.194',
	    user     : 'root',
	    password : 'lyj5654582',
	    database : 'restaurant_db'
	 });
	connection.connect();
	var hasManager = "Select id as num from menu where manager_id =?";
	connection.query(hasManager,[req.params.manager_id],function(err,rows,fields){
		if (err) throw err;
		if(rows.length != 0){
			var menuID = rows[0].num;
			connection.query("select * from `dish` where id in (select dish_id from `set_meal_has_dish` where set_meal_id = ? and menu_id = ?)",[setmealID,menuID],function(err,rows,fields){
				if(err) throw err;
				res.send(JSON.stringify(rows));
				connection.end();
			});
		}else{
			res.send(JSON.stringify([]));
			connection.end();
		}
	});
});

app.get('/countinfo/:manager_id',function(req,res){
	var managerID = req.params.manager_id;
	res.setHeader('Content-Type',"application/json;charset=utf-8");
	res.setHeader("Access-Control-Allow-Origin","*");
	res.setHeader("Access-Control-Allow-Methods","POST,GET,DELETE,OPTION");
	console.log("----------");
	console.log("get count info");
	console.log(managerID);
	console.log("----------");
	var connection = mysql.createConnection({
	    host     : '121.42.210.194',
	    user     : 'root',
	    password : 'lyj5654582',
	    database : 'restaurant_db'
	 });
	connection.connect();
	connection.query("select count(*) as count_call from `call_message` where manager_id = ?",[managerID],function(err,rows,fields){
		if (err) throw err;
		var countCall = rows[0].count_call;
		connection.query("select count(*) as count_order,sum(spend) as sum_order from `order` where manager_id = ?",[managerID],function(err,rows,fields){
			if (err) throw err;
			var countOrder = rows[0].count_order;
			var sumOrder = rows[0].sum_order;
			connection.query("select count(*) as count_table from dining_table where manager_id = ? and status = ?",[managerID,1],function(err,rows,fields){
				if (err) throw err;
				var countTable = rows[0].count_table;
				var toSend = {count_order:countOrder,count_call:countCall,count_table:countTable,sum_order:sumOrder};
				res.send(toSend);
			});
		})
	});
});

app.get('/alldish/:manager_id/',function(req,res){
	var managerID = req.params.manager_id;
	var connection = mysql.createConnection({
    host     : '121.42.210.194',
    user     : 'root',
    password : 'lyj5654582',
    database : 'restaurant_db'
  });	
	connection.connect();
	var getMenuId = "select id as num from menu where manager_id = ?";
	res.setHeader('Content-Type',"application/json;charset=utf-8");
	res.setHeader("Access-Control-Allow-Origin","*");
	res.setHeader("Access-Control-Allow-Methods","POST,GET");
	connection.query(getMenuId,[managerID],function(err,rows,fields){
		if (err) throw err;
		if (rows.length != 0){
			var menuID = rows[0].num;
			var getAllDish = "Select * from `dish` where menu_id = ?";
			connection.query(getAllDish,[menuID],function(err,rows,fields){
				if (err) throw err;
				res.send(JSON.stringify(rows));
			});
		}
	});
});
