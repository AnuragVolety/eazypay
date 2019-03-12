<?php 
	require_once 'DbOperation.php';
	$response = array(); 

	if($_SERVER['REQUEST_METHOD']=='POST'){

		$email = $_POST['email'];
		$token = $_POST['token'];
		$name = $_POST['name'];
		$phone = $_POST['phone'];
		$vehicle = $_POST['vehicle'];
		$registration = $_POST['registration'];

		$db = new DbOperation(); 

		$result = $db->registerDevice($email,$token,$name,$phone,$vehicle,$registration);

		if($result == 0){
			$response['error'] = false; 
			$response['message'] = 'Device registered successfully';
		}elseif($result == 2){
			$response['error'] = true; 
			$response['message'] = 'Device already registered';
		}else{
			$response['error'] = true;
			$response['message']='Device not registered';
		}
	}else{
		$response['error']=true;
		$response['message']='Invalid Request...';
	}

	echo json_encode($response);