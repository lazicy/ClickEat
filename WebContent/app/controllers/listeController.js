clickEat.controller('listeController', ['$scope', '$location', '$timeout', 'userService', 'restoranFactory', 'vozilaService', 'korpaService', 
    function($scope, $location, $timeout, userService, restoranFactory, vozilaService, korpaService) {


        var init = function() {
            $scope.isAdmin = false;
            $scope.isKupac = false;
            $scope.isDostavljac = false;
            $scope.isLoggedIn = false;
            $scope.activeTab = 1;
            $scope.artikli = [];
            $scope.restorani = [];
            $scope.vozila = [];
            $scope.users = [];

            $scope.expandJela = true;
            $scope.expandPica = true;

            $scope.tipoviVozila = { 
                                    1: "Bicikl",
                                    2: "Skuter",
                                    3: "Automobil"
             };


            $scope.expand = [];
            $scope.expandUsers = [];

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


        };

        var checkRole = function() {
            if (userService.activeUser.role == 2) {
                $scope.isAdmin = true;
                $scope.isLoggedIn = true;
                $scope.user = userService.activeUser;
            } else if (userService.activeUser.role == 0) {
                $scope.isKupac = true;
                $scope.isLoggedIn = true;
                $scope.user = userService.activeUser;
            } else if (userService.activeUser.role == 1) {
                $scope.isDostavljac = true;
                $scope.isLoggedIn = true;
                $scope.user = userService.activeUser;
            } else if (userService.activeUser.role == -1) {
                $scope.isLoggedIn = false;
                userService.activeUser = { lastname: 'Guest', role: -1};
                $scope.user = userService.activeUser;
            }
            console.log("CheckRole(): " + userService.activeUser.role);
            return userService.activeUser.role;
        };

        $scope.artikliClick = function() {
            $scope.activeTab = 1;
           
        };

        $scope.restoraniClick = function() {
            $scope.activeTab = 2;
           
        };
        
        $scope.vozilaClick = function() {
            $scope.activeTab = 3;
           
        };
        
        $scope.usersClick = function() {
            $scope.activeTab = 4;
           
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

        var checkHash = function() {
            var hash = $location.hash();

            if (hash === "artikli") {
                $scope.activeTab = 1;
            } else if (hash == "restorani") {
                $scope.activeTab = 2;
            } else if (hash == "vozila") {
                if ($scope.isDostavljac || $scope.isAdmin) {
                    $scope.activeTab = 3;
                }
            } else if (hash == "users") {
                if($scope.isAdmin) {
                    $scope.activeTab = 4;
                }
            }


        };

        var loadStuff = function() {
            loadArtikli();
            loadRestorani();

            if ($scope.isAdmin || $scope.isDostavljac) {
                loadVozila();
            }

            if ($scope.isAdmin) {
                loadUsers();
            }

        }
        
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

        
        var loadRestorani = function() {
            restoranFactory.getRestoranListList().then(
                function(response) {
                    if (response.status == 200) {
                        $scope.restorani = response.data;
                    }
                }, function(error) {
                    console.log(error);
                }

            );

            restoranFactory.getRestoranList().then(
                function(response) {
                    if (response.status == 200) {
                        $scope.restoraniMap = response.data;
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

        var loadUsers = function() {
            userService.getUserMap(userService.activeUser.username, userService.activeUser.password).then(
                function(response) {
                    if (response.status == 200) {
                        $scope.users = response.data;
                    }
                }, function(error) {
                    console.log(error);
                }

            );
        }

       

        $scope.expandOrToggleJela = function() {
            $scope.expandJela = !$scope.expandJela;
        };

        
        $scope.expandOrTogglePica = function() {
            $scope.expandPica = !$scope.expandPica;
        };

        $scope.expandOrToggle = function(i) {
            $scope.expand[i] = !$scope.expand[i];
        };

        $scope.expandOrToggleUsers = function(i) {
            $scope.expandUsers[i] = !$scope.expandUsers[i];
        }

        $scope.noviRestoran = function() {
            $location.path("/restoranmanipulation");
            $location.hash("new");
        };

        $scope.noviArtikal = function() {
            $location.path("/artikalmanipulation");
            $location.hash("new");
        };

        $scope.izmenaRestorana = function(id) {
            $location.path("/restoranmanipulation");
            $location.hash("modify" + id);
        };

        $scope.izmenaArtikla = function(id) {
            $location.path("/artikalmanipulation");
            $location.hash("modify" + id);
        };


        $scope.novoVozilo = function() {
            $location.path("/vozilomanipulation");
            $location.hash("new");
        }

        $scope.izmenaVozila = function(id) {
            $location.path("/vozilomanipulation");
            $location.hash("modify" + id);
        };

    
        $scope.brisanjeRestorana = function(id) {
            restoranFactory.deleteRestoran(id).then(
                function(response) {
                    if (response.status == 200) {
                        console.log("success");
                        $scope.restorani[id].aktivan = false;
                    }
                }, function(error) {
                    console.log(error);
                }
            );
        };

        $scope.brisanjeArtikla = function(id) {
            restoranFactory.deleteArtikal(id).then(
                function(response) {
                    if (response.status == 200) {
                        console.log("success");
                        $scope.artikli[id].aktivan = false;
                    }
                }, function(error) {
                    console.log(error);
                }
            );
        };

        $scope.brisanjeVozila = function(id) {
            vozilaService.removeVozilo(id).then(
                function(response) {
                    if (response.status == 200) {
                        console.log("success");
                        $scope.vozila[id].aktivan = false;
                    }
                }, function(error) {
                    console.log(error);
                }
            );
        };

        $scope.goToUserProfile = function(username) {
            $location.path("/profile");
            $location.hash(username);
        };

        $scope.zaduziVozilo = function(id) {
            userService.zaduziVozilo(userService.activeUser.username, id).then(
                function(response) {
                    if (response.status == 200) {
                        console.log("success");
                        userService.activeUser.vozilo = $scope.vozila[id];
                        $scope.vozila[id].uUpotrebi = true;

                    }
                }, function(error) {

                }
            );

        };
    

        init();

    }]);