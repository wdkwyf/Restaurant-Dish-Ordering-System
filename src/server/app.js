var express = require('express');
var app = module.exports = express();
fs = require('fs');
var mysql      = require('mysql');
var bodyParser = require('body-parser');
var rt = require('./gettime')
var constructID = require('./constructID');

require('./test');
require('./manageredit');
require('./customer');
require('./waiter');
require('./managersearch');
require('./managerlogin');

var server = app.listen(3000, function () {
  var host = server.address().address;
  var port = server.address().port;

  console.log('Restaurant Server at http://%s:%s', host, port);
});

