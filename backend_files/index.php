<?php

    //server base url
		$servername = "127.0.0.1";
		//server user name
    $username = "root";
    //server login password
    $password = "";
    //database name
    $dbname = "univote";
    $conn = new mysqli($servername, $username, $password, $dbname);
    // Check connection
    if ($conn->connect_error) {
      die("Connection failed: " . $conn->connect_error);
    }

    header("Access-Control-Allow-Orgin: *");
    header("Access-Control-Allow-Methods: *");
    header("Content-Type: application/json");

    //request url
    $requestUri = explode('/', trim($_SERVER['REQUEST_URI'],'/'));
    $method = $_SERVER['REQUEST_METHOD'];

    //this method is used for getting request from the app and finding data from the database and returning a response.
    if(strtolower($method) == 'post')
    {

       //this condition is used to check the login then check for the voterID.
        if($requestUri[count($requestUri) - 1] == 'login')
        {
            $voter_id = $_POST['voter_id'];
            $res_arr = array();

     $sql = "SELECT * FROM users where `voter_id` = '$voter_id'";
            if($result = $conn->query($sql))
            {
                if ($result->num_rows > 0) {
                   $row = $result -> fetch_array(MYSQLI_ASSOC);
                   $result -> free_result();

                   $res_arr["status"] = true;
                   $res_arr["id"] = $row["id"];
				   		 		 $res_arr["vote_casted"] = $row["vote_casted"];
                }
                else
                {
                  $res_arr["status"] = false;
                }
            }
            else
            {
              $res_arr["status"] = false;
            }
            echo json_encode($res_arr);
        }
        //this condition is used to record the vote for a VoterID in the database
        else if($requestUri[count($requestUri) - 1] == 'do_vote')
        {
            $user_id = $_POST['user_id'];
            $candidate = $_POST['candidate'];
            $res_arr = array();

            $sql = "UPDATE users SET vote_casted = 'Yes' WHERE id = ".$user_id;
            if($result = $conn->query($sql))
            {
                $res_arr["status"] = true;
            }
            else
            {
              $res_arr["status"] = false;
            }
            echo json_encode($res_arr);
// add 1 to the candidate counter to count the vote.
			$sql1 = "UPDATE candidates SET counter = counter+1 WHERE name = '".$candidate."'";
            if($result1 = $conn->query($sql1))
            {
                $res_arr1["status"] = true;
            }
            else
            {
              $res_arr1["status"] = false;
            }
            echo json_encode($res_arr1);
        }
    }
    //this method is used to get all candidates from server database and send them to app
    else if(strtolower($method) == 'get')
    {

        if($requestUri[count($requestUri) - 1] == 'get_candidates')
        {
            $res_arr = array();
            $sql = "SELECT * FROM candidates";

            if($result = $conn->query($sql))
            {
                if ($result->num_rows > 0) {
                    $res_arr = array();
                    while($row = $result -> fetch_array(MYSQLI_ASSOC)){

                        $res_arr[] = $row;

                    }

                }
            }
            echo json_encode($res_arr);
        }
    }

?>
