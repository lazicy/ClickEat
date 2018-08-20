clickEat.controller('voziloManipulationController', ['$scope', '$location', '$timeout', 'userService', 'vozilaService', function($scope, $location, $timeout, userService, vozilaService) {

    var init = function() {

        $scope.modify = false;
        $scope.new = false;

        $scope.tipoviVozila = { 
            1: "Bicikl",
            2: "Skuter",
            3: "Automobil"
        };

        $scope.izmena = [];


        $scope.isAdmin = false;
        $scope.isKupac = false;
        $scope.isDostavljac = false;
        $scope.isLoggedIn = false;

        var roleInit = checkRole();
        
            if (roleInit == -2 ) {
                getUserOnSession();
               
                var callback  = function(){
                   checkRole();
                   checkHash();
        
                }
                $timeout(callback, 500);
            } else {
                checkHash();
            }


        
    };

    var checkRole = function() {
        if (userService.activeUser.role == 2) {
            $scope.isAdmin = true;
            $scope.isLoggedIn = true;
           
        } else if (userService.activeUser.role == 0) {
            $scope.isKupac = true;
            $scope.isLoggedIn = true;
            redirect();
        } else if (userService.activeUser.role == 1) {
            $scope.isDostavljac = true;
            $scope.isLoggedIn = true;
            redirect();
        } else if (userService.activeUser.role == -1) {
            $scope.isLoggedIn = false;
            userService.activeUser = { lastname: 'Guest', role: -1};
             redirect();
        }
        console.log("CheckRole(): " + userService.activeUser.role);
        return userService.activeUser.role;
    };

    var getUserOnSession = function() {
        userService.getUserOnSession().then(
            
            function(response) {
                if (response.status == 200 ) {

                    userService.activeUser = response.data;
                    
                } else {
                    userService.activeUser = { lastname: 'Guest', role: -1};
                }
            }, 

            function (error) {
                userService.activeUser = { lastname: 'Guest', role: -1};
                console.log(error);
            }

        );
    };

    var redirect = function() {
        $location.path("/");
        $location.hash("");
        
    }
    var checkHash = function() {

        var hash = $location.hash();
         // dodavanje nvog
         if (hash === "new") {
            $scope.new = true;
        // izmena
        } else if (hash.includes("modify")) {
            $scope.modify = true;
            var id = hash.replace("modify", "");
            id = parseInt(id);
            console.log("Id sa hasha: " + id);
            if (!isNaN(id)) {

                getVoziloById(id);
                    
                
            } else {
                redirect();
                console.log("nije dobar hash.")
            }


        } else {
            redirect();
        }
    }

    var getVoziloById = function(id) {
        vozilaService.getVoziloById(id).then(
            function(response) {
                if (response.status == 200) {
                    $scope.izmena = response.data;
                    $scope.tipStr = String($scope.izmena.tip);
                } else {
                    redirect();
                }
            }, function(error) {
                console.log(error);
                redirect();
            }
        );
    };

    $scope.registracijaValidate = function() {
        $scope.regerror = false;
        if ($scope.modify) {
            v = $scope.izmena.registracija;
        } else if ($scope.new) {
            v = $scope.novi.registracija;
        }

        var letters = /^[A-Za-z]+$/;
        var grad = v.substring(0,2);
        var cifre = v.substring(2,5);
        var slova = v.substring(5);

        if(!grad.match(letters)) {
            $scope.regerror = true;
        }

        if(isNaN(cifre)) {
            $scope.regerror = true;
        }

        if(!slova.match(letters)) {
            $scope.regerror = true;
        }

        if(v.length != 7) {
            $scope.regerror = true;
        }

    };

    $scope.addVozilo = function() {
        if ($scope.novi.tip == 1) {
            $scope.novi.registracija = "-";
        }

        vozilaService.addVozilo($scope.novi).then(
            function(response) {
                if (response.status == 200) {
                    console.log("succes");
                    $location.path("/liste");
                    $location.hash("vozila");
                }
            }, function(error) {
                console.log(error);
            }

        );
    };

    $scope.modifyVozilo = function() {
        
        vozilaService.modifyVozilo($scope.izmena).then(
            function(response) {
                if (response.status == 200) {
                    console.log("success");
                    $location.path("/liste");
                    $location.hash("vozila");
                }
            }, function(error) {
                console.log(error);
            }

        );
    }

   

    init();

}]);