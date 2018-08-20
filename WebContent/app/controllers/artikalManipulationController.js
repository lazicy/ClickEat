clickEat.controller("artikalManipulationController", ['$scope', '$location', '$timeout', 'restoranFactory', 'userService', function($scope, $location, $timeout, restoranFactory, userService) {

    var init = function() {
        console.log("ArtikalManipulationController.Init");
       
        $scope.new = false;
        $scope.modify = false;

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
                   loadStuff();
        
                }
                $timeout(callback, 500);
            } else {
                checkHash();
                loadStuff();
            }

       

    }

    var loadStuff = function() {
        
        restoranFactory.getRestoranList().then(
            function(response) {
                if (response.status == 200) {
                    $scope.restoranList = response.data;
                }

            }, function(error) {

            }



        );

    };

    var checkHash = function() {
        var hash = $location.hash();
        if (hash === "new") {
           $scope.new = true;
       // izmena
       } else if (hash.includes("modify")) {
            $scope.modify = true;
            var id = hash.replace("modify", "");
            id = parseInt(id);
            console.log("Id sa hasha: " + id);
            if (!isNaN(id)) {

               restoranFactory.getArtikalById(id).then(
       
                   function(response) {
                       if (response.status == 200) {
                           $scope.izmena = response.data;
                           $scope.tipStr = String($scope.izmena.tip);
                           $scope.getRestoranById($scope.izmena.restoranId);
                       } else {
                           
                            redirect();
                       }
                   },
                   function (error) {
                       
                      redirect();
                       console.log(error);
                   }
       
               );
                   
           } else {
               redirect();
               console.log("nije dobar hash.")
           }
        } else {
            redirect();
        }

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

    $scope.addArtikal = function() {
        console.log("Selected " + $scope.selected.id);
        $scope.novi.restoranId = $scope.selected.id;
        
        restoranFactory.addArtikal($scope.novi).then(
            function(response) {
                if (response.stats == 200) {
                    console.log("Success");
                }

                $location.path('/restoran');
                $location.hash($scope.novi.restoranId);
            }, 
            function (error) {
                console.log(error);
            }

        );
    
    }

    $scope.modifyArtikal = function() {
        
        delete $scope.izmena.restoran;
        $scope.izmena.tip = parseInt($scope.tipStr);
        restoranFactory.modifyArtikal($scope.izmena).then(
            function(response) {
                if (response.status == 200) {
                    console.log("Success");
                }
                
                $location.path('/restoran');
                $location.hash($scope.izmena.restoranId);
            }, function (error) {
                console.log(error);
            }

        );
    
    };

    $scope.getRestoranById = function(id) {
        restoranFactory.getRestoranById(id).then(
    
            function(response) {
                if (response.status == 200) {
                    $scope.izmena.restoran = response.data;
                }
            }, 
            function (error) {
                console.log(error);
            }

        );

    }; 

    var redirect = function() {
        $location.path("/");
        $location.hash("");
    };

    init();

}]);