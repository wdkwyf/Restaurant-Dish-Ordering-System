var app = require('./app')
var mysql      = require('mysql');
var bodyParser = require('body-parser');
var rt = require('./gettime');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
  extended: true
}));
var constructID = require('./constructID');

app.post('/console/dish/:manager_id',function(req,res){
	var managerID = req.params.manager_id;
	var editBody = req.body.edit;
	var connection = mysql.createConnection({
	    host     : '121.42.210.194',
	    user     : 'root',
	    password : 'lyj5654582',
	    database : 'restaurant_db'
	 });	
	connection.connect();
	console.log(editBody);
	connection.query("select id as menu_id from menu where manager_id = ?",[managerID],function(err,rows,fields){
		if (err) throw err;
		if (rows.length != 0){
			var menuID = rows[0].menu_id;
			for (var i = 0; i<editBody.length;i++){
				var editId = editBody[i].dishid;
				var editInfo = editBody[i].info;
				// console.log(editId,editInfo);
				connection.query("Update `dish` set ? where id = ? and menu_id = ?",[editInfo,editId,menuID],function(err,rows,fields){
					console.log("edit info : ",i,editInfo,editId);
					if (err) throw err;
				});
			}
		}else{
			connection.end();
		}
	});
	res.send("console ~");
});

app.delete('/console/dish/:manager_id',function(req,res){
	var managerID = req.params.manager_id;
	var dltid  = req.body.dishId;
	res.setHeader('Content-Type',"application/json;charset=utf-8");
	res.setHeader("Access-Control-Allow-Origin","*");
	res.setHeader("Access-Control-Allow-Methods","POST,GET,DELETE,OPTION");
	console.log("----------");
	console.log(managerID,dltid);
	console.log("----------");
	var connection = mysql.createConnection({
	    host     : '121.42.210.194',
	    user     : 'root',
	    password : 'lyj5654582',
	    database : 'restaurant_db'
	 });
	connection.connect();
	connection.query("select id as menu_id from menu where manager_id = ?",[managerID],function(err,rows,fields){
		if (err) throw err;
		if(rows.length != 0 ){
			var menuID = rows[0].menu_id;
			connection.query("delete from `set_meal_has_dish` where dish_id = ? and menu_id = ?",[dltid,menuID],function(err,rows,fields){
				if (err) throw err;
				connection.query("delete from `dish` where id = ? and menu_id = ?",[dltid,menuID],function(err,rows,fields){
					if (err) throw err;
				});
			});
		}else{
			connection.end();
		}
	});
	res.send("delete over~");
});

app.post('/newdish/:manager_id',function(req,res){
	var managerID = req.params.manager_id;
	var dishList = req.body.dish;
	console.log(req.body);
	res.setHeader('Content-Type',"application/json;charset=utf-8");
	res.setHeader("Access-Control-Allow-Origin","*");
	res.setHeader("Access-Control-Allow-Methods","POST,GET");

	var connection = mysql.createConnection({
	    host     : '121.42.210.194',
	    user     : 'root',
	    password : 'lyj5654582',
	    database : 'restaurant_db'
	 });	
	connection.connect();
	var hasManager = "Select id as num from menu where manager_id =?";
	connection.query(hasManager,[managerID],function(err,rows,fields){
		if (err) throw err;
		if (rows.length != 0){
			var menuID = rows[0].num;
			console.log(menuID);
			var dishInsertData = [];
			connection.query("select id as num from dish where menu_id = ?",[menuID],function(err,rows,fields){
				if (err) throw err;
				var idList = [];
				for(var i=0;i<rows.length;i++){
					idList.push(parseInt(rows[i].num));
				}
				var dishSum = Math.max.apply(Math,idList);
				for(var i = 0;i<dishList.length;i++){
					var oneData = [];
					oneData.push(constructID.intToDishid(dishSum+i+1,1),dishList[i].name,dishList[i].tag,managerID,dishList[i].dish_type,dishList[i].waiting_time,dishList[i].spend);
					dishInsertData.push(oneData);
				}
				var insertDish = "Insert into dish(id,name,tag,menu_id,dish_type,waiting_time,spend) VALUES ?";
				connection.query(insertDish,[dishInsertData], function(err, results) {
		  		if (err) throw err;
				});
				connection.end();
			});
			res.send("Query Complete!");
		}else{
			connection.end();
			res.send("No such manager!");
		}
	});
});

app.post('/newsetmeal/:manager_id',function(req,res){
	var managerID = req.params.manager_id;
	var setmeal = req.body.setmeal;
	var hasRe = req.body.has;
	console.log(req.body);
	res.setHeader('Content-Type',"application/json;charset=utf-8");
	res.setHeader("Access-Control-Allow-Origin","*");
	res.setHeader("Access-Control-Allow-Methods","POST,GET");
	var connection = mysql.createConnection({
	    host     : '121.42.210.194',
	    user     : 'root',
	    password : 'lyj5654582',
	    database : 'restaurant_db'
	 });	
	connection.connect();
	var hasManager = "Select id as num from menu where manager_id =?";
	connection.query(hasManager,[managerID],function(err,rows,fields){
		if (err) throw err;
		if (rows.length != 0){
			var menuID = rows[0].num;
			console.log(menuID);
			var setInsertData = [];
			connection.query("select id as sum from set_meal where menu_id = ?",[menuID],function(err,rows,fields){
				if (err) throw err;
				var setList = [];
				for(var i = 0;i<rows.length;i++){
					setList.push(parseInt(rows[i].sum));
				}
				setMax = Math.max.apply(Math,setList);
				setmealIDInt = setMax + 1;
				var setmealID = setmealIDInt.toString();
				console.log(setmealID);
				var insertSetMeal = "Insert into set_meal set ?";
				var insertData = {id:setmealID,menu_id:menuID,spend:setmeal.spend,set_name:setmeal.set_name};
				connection.query(insertSetMeal,insertData, function(err, results) {
		  			if (err) throw err;
				});

				var insertHas = [];
				for(var i = 0; i<hasRe.length;i++){
					var oneData = [];
					oneData.push(setmealID,hasRe[i].dishid,hasRe[i].num,menuID);
					insertHas.push(oneData);
				}
				console.log(insertHas);
				connection.query("insert into set_meal_has_dish(set_meal_id,dish_id,number,menu_id) VALUES ?",[insertHas],function(err,rows,fields){
					if (err) throw err;
				});
				connection.end();
			});
			
			res.send("Query Complete!");
		}else{
			connection.end();
			res.send("No such manager!");
		}
	});
});