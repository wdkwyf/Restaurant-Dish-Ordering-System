var app = require('./app')
var mysql      = require('mysql');
var bodyParser = require('body-parser');
var rt = require('./gettime');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
  extended: true
}));
var constructID = require('./constructID');

app.get('/menulist/:manager_id/',function(req,res){
	console.log("Type : " + req.query.dishtype)
	var connection = mysql.createConnection({
    host     : '121.42.210.194',
    user     : 'root',
    password : 'lyj5654582',
    database : 'restaurant_db'
  });	

	connection.connect();
	var hasManager = "Select id as num from menu where manager_id =?";
	res.setHeader('Content-Type',"application/json;charset=utf-8");
	res.setHeader("Access-Control-Allow-Origin","*");
	res.setHeader("Access-Control-Allow-Methods","POST,GET");
	connection.query(hasManager,[req.params.manager_id],function(err,rows,fields){
		if (err) throw err;
		if (rows.length != 0){
			var menu_id = rows[0].num;
			if (req.query.dishtype != "setmeal"){
			//	console.log("111")
				var getMenulist = "Select id,name,spend,dish_type,waiting_time from dish where menu_id=? and dish_type = ?";
				connection.query(getMenulist,[menu_id,req.query.dishtype],function(err,rows,fields){
					if (err) throw err;
					var reMenu = {'menu':rows};
					res.send(JSON.stringify(reMenu));
					connection.end();
				});
			}else{
			//	console.log("222");
				var getSetMeal = "Select id,spend,set_name from set_meal where menu_id = ?";
				connection.query(getSetMeal,[menu_id],function(err,rows,fields){
					if (err) throw err;
					var reMenu = {'menu':rows};
					res.send(JSON.stringify(reMenu));
					connection.end();
				});
			}
		}else{
			console.log("No User");
			res.send("No such manager ID!");
		}
	});
});

app.post('/orderlist/',function(req,res){
	res.setHeader('Content-Type',"application/json;charset=utf-8");
	res.setHeader("Access-Control-Allow-Origin","*");
	res.setHeader("Access-Control-Allow-Methods","POST");
	var orderID = rt.getDateTime();
	var dishes = req.body.dish;
	var set_meal = req.body.setmeal;
	var totalMoney = req.body.orderSum.totPrice;
	var dining_table_id = req.body.table.tableId;
	var dishCount = req.body.orderSum.count;
	var customerTotal = req.body.table.customerCount;
	console.log(dishes,dishCount);
	//var getOrderID = 
	//var newOrder = "insert into order set ?",json,function(err,rows,fields){})
	var dishInsertData = [];
	var setInsertData = [];

	var connection = mysql.createConnection({
    host     : '121.42.210.194',
    user     : 'root',
    password : 'lyj5654582',
    database : 'restaurant_db'
  });	
	connection.connect();

	var insertOrderInfo = "insert into `order` set ?";// id status spend order_time dining_table_id
	var inOrder = {id:orderID,status:0,spend:totalMoney,order_time:orderID,dining_table_id:dining_table_id,manager_id:"0000"};
	console.log(inOrder);

	connection.query(insertOrderInfo,inOrder,function(err,rows,fields){
		if (err) throw err;	
	});

	// var getSpend = "select spend from dish where id = ?";

	if (dishes.length != 0){
		for(var i =0;i<dishes.length;i++){
			var oneData = [];
			oneData.push(orderID,dishes[i].dishId,dishes[i].quantity);
			dishInsertData.push(oneData);
			// connection.query(getSpend,[dishes[i].dishId],function(err,rows,fields){
			// 	if (err) throw err;
			// 	totalMoney += rows[0].spend*dishes[i].quantity;
			// });
		}
		var insertDish = "insert into order_has_dish(order_id,dish_id,number) VALUES ?";

		connection.query(insertDish,[dishInsertData], function(err, results) {
	  		if (err) throw err;
		});
	}

	if (set_meal.length != 0){
		for(var i = 0;i<set_meal.length;i++){
			var oneData = [];
			oneData.push(orderID,set_meal[i].setmealId,set_meal[i].quantity);
			setInsertData.push(oneData);
		}
		var insertSetMeal = "insert into order_has_set_meal(order_id,set_meal_id,number) VALUES ?";

		connection.query(insertSetMeal,[setInsertData],function(err,results){
			if (err) throw err;
		});
	}
	connection.end();
	res.send(JSON.stringify({"waiting_time":30}));
});
