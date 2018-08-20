clickEat.controller('mainController', ['$scope', '$location', '$timeout', 'userService', 'restoranFactory', 'korpaService', 'vozilaService',
                                        function($scope, $location, $timeout, userService, restoranFactory, korpaService, vozilaService) {
    
    var init = function() {

        console.log("MainController.Init");
        $scope.isAdmin = false;
        $scope.isKupac = false;
        $scope.isDostavljac = false;
        $scope.isLoggedIn = false;
        $scope.zauzet = false;
        $scope.expandZaduziVozilo = false;
        
        loadArtikli();
        
        if (checkRole() != -2) {
            
            $scope.user = userService.activeUser;
            console.log("userService.activeUser = " + userService.activeUser.username);
            
        } else {
            userService.getUserOnSession().then(
            
                function(response) {
                    if (response.status == 200 ) {

                        userService.activeUser = response.data;
                        $scope.user = userService.activeUser;
                        console.log("AFTER getUserOnSession(): userService.activeUser = " + userService.activeUser.username);
            
                        checkRole();
    
                    } else {
                        console.log("No user on session.");
                        userService.activeUser = { lastname: 'Guest', role: -1};
                        $scope.user = userService.activeUser;
                        checkRole();
                       
                    }
                }, 
    
                function (error) {
                    userService.activeUser = { lastname: 'Guest', role: -1};
                    checkRole();
                    console.log(error);
                }
    
            );
        }

        // restoranFactory.getRestoranList().then(

        //     function(response) {
        //         if (response.status == 200) {
        //             $scope.restoranList = response.data;
        //         }

        //         console.log(response.data);
                
        //     },
        //     function (error) {
        //         console.log("error");
        //     }

        // );

        

    };



    var checkRole = function() {
        if (userService.activeUser.role == 2) {
            $scope.isAdmin = true;
            $scope.isLoggedIn = true;
        } else if (userService.activeUser.role == 0) {
            $scope.isKupac = true;
            $scope.isLoggedIn = true;
        } else if (userService.activeUser.role == 1) {
            $scope.isDostavljac = true;
            $scope.isLoggedIn = true;
            loadPorudzbine();
            loadVozila();
        } else if (userService.activeUser.role == -1) {
            $scope.isLoggedIn = false;
        }

        return userService.activeUser.role;
    };

    var loadPorudzbine = function() {
        korpaService.getPorudzbineMap().then(
            function(response) {
                if (response.status == 200) {
                    $scope.porudzbineMap = response.data;
                    checkZauzet();
                }
            }, function(error) {
                console.log(error);
            }

        );
    };


    var loadArtikli = function() {
        restoranFactory.getArtikalList().then(
            function(response) {
                if (response.status == 200) {
                    $scope.artikli = response.data;
                }
            }, function(error) {
                console.log(error);
            }

        );
    };

    var loadVozila = function() {
        
        vozilaService.getVozilaMap().then(
            function(response) {
                if (response.status == 200) {
                    $scope.vozila = response.data;
                }
            }, function(error) {
                console.log(error);

            }
        );

    };

    var checkZauzet = function() {
        var porudzbine = $scope.user.porudzbine;
        for (var i = 0; i < porudzbine.length; i++) {
            if (porudzbine[i].statusPorudzbine == 2) {
                $scope.aktivna = porudzbine[i];
                $scope.zauzet = true;
            }
        }
    }

   

   
    $scope.dostavi = function(aktivna) {
        aktivna.statusPorudzbine = 4;

        korpaService.dostavi(aktivna.idPorudzbine, userService.activeUser.username, aktivna.kupacUsername).then(
            function(response) {
                if (response.status == 200) {
                    console.log("success");
                    $scope.zauzet = false;
                    
                }
            }, function(error) {
                console.log(error);
            }
        );

    };

    $scope.razduziVozilo = function() {

        userService.razduziVozilo(userService.activeUser.username, userService.activeUser.vozilo.id).then(
            function(response) {
                if (response.status == 200) {
                    console.log("success");
                    userService.activeUser.vozilo = null;
                    $scope.vozila[id].uUpotrebi = false;

                }
            }, function(error) {

            }
        );

    };

    $scope.izaberiVozilo = function() {
        $location.path("/liste");
        $location.hash("vozila");
    };

    $scope.preuzmiPorudzbinu = function(idPorudzbine) {

        userService.preuzmiPorudzbinu(userService.activeUser.username, idPorudzbine).then(
            function(response) {
                if (response.status == 200) {
                    console.log("success");
                    $scope.porudzbineMap[idPorudzbine].statusPorudzbine = 2;
                    userService.activeUser.porudzbine.push($scope.porudzbineMap[idPorudzbine]);
                    checkZauzet();
                }
            }, function(error) {

            }
        );
    };





    
    init();
    
    
      
   

   
    
}]);