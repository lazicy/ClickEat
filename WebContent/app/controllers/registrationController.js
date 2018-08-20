clickEat.controller('registrationController', function($scope, $location, userService) {

    function init() {
        console.log('RegistrationController.Init');

        userService.getUserOnSession().then(
            
            function(response) {
                if (response.status == 200 ) {
                    userService.activeUser = response.data;
                    console.log("UserOnSession: " + response.data);
                    $location.path('/');

                }
            }, 

            function (error) {
                console.log(error);
            }

        );


        $scope.phoneValid = true;
        $scope.distinctUsername = true;
        $scope.distinctEmail = true;

    }

    init();

    $scope.phoneNumberValidation =  function (user) {
        // phone number + conversion 
        if (user == undefined) {
            return;
        }

        if (user.phone != undefined && user.phone != "") {
            user.phone = user.phone.replace("+", "00");
            user.phone = user.phone.replace("-", "");
            user.phone = user.phone.replace("/", "");
            user.phone = user.phone.replace(".", "");
            user.phone = user.phone.replace(",", "");
            
        }
        
        if (isNaN(user.phone)) {
            $scope.phoneValid = false;
        } else {
            $scope.phoneValid = true;
        }
    };

    $scope.register = function(user) { 

        // setting date 
        var d = new Date();

        user.registrationDate = d.getDate() + '/' + d.getMonth() + '/' + d.getFullYear();
        console.log("Client: registrationDate: " + user.registrationDate);


        // parsing phone
        
        if (isNaN(user.phone)) {
            console.log("registrationController:register:user.phone: " + user.phone);
            $scope.phoneValid = false;
            return;
        } else {
            console.log("ELSE:registrationController:register:user.phone: " + user.phone);
            
        }

       
        console.log($scope.user.fisrtname);
        console.log($scope.user.lastname);
        
         // register user REST call
        userService.register(user).then(

            function (response) {

                if (response.status == 200) {

                    if (response.data === "OK") {
                       
                        console.log("uspesna registracija");
                        
                        $location.path('/');

                    } else if (response.data === "USERNAME") {
                        $scope.distinctUsername = false;
                        
                    } else if (response.data === "EMAIL") {
                        $scope.distinctEmail = false;
                    }
                    
              } 
            },
                


            function (error) {
                console.log(error);
            }

        );
    }
});
        
       


       