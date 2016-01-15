module.exports = {
	intToDishid : function(k,j){
		if(j == 0){
	        if(k < 10){
	            return "100"+k.toString();
	        }else if(k < 100){
	            return "10"+k.toString();
	        }else if(k<1000){
	            return "1"+k.toString();
	        }else{
	            return "Boom shaka laka";
	        }
	    }else{
	    	if(k < 10){
	            return "000"+k.toString();
	        }else if(k < 100){
	            return "00"+k.toString();
	        }else if(k<1000){
	            return "0"+k.toString();
	        }else{
	            return "Boom shaka laka";
	        }
	    }
    }
}