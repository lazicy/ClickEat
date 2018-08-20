clickEat.controller('restoranController', ['$scope', '$location', 'restoranFactory', 'userService', 'korpaService', function($scope, $location, restoranFactory, userService, korpaService) {
    var init = function() {

        $scope.isFavRestoran = false;
        $scope.isAdmin = false;
        $scope.isKupac = false;
        $scope.isDostavljac = false;
        $scope.isLoggedIn = false;
        $scope.lista = false;
        
        if (checkRole() != -2) {
            
            $scope.user = userService.activeUser;
            console.log("userService.activeUser = " + userService.activeUser.username);
            
        } else {
            userService.getUserOnSession().then(
            
                function(response) {
                    if (response.status == 200 ) {

                        userService.activeUser = response.data;
                        userService.activeUser.stavke = [];

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
        
        var hash = $location.hash();
        if (hash === "") {
            getRestoranList();
            $scope.lista = true;
        }
        else if (!isNaN(hash)) {
            
            $scope.getRestoranById(hash);
                
        } else {

            $location.path('/');
        }
    }

    
    $scope.deleteRestoran = function() {
        
        if ($scope.restoran != null) {
            restoranFactory.deleteRestoran($scope.restoran.id).then(
                function(response) {
                    if (response.status = 200) {
                        $location.path('/');
                    }

                }, function(error) {

                    console.log(error);

                }


            );

        }

    };

    $scope.deleteArtikal = function(artikal) {

        restoranFactory.deleteArtikal(artikal.id).then(
            function(response) {
                if (response.status == 200) {
                    console.log("Successfully deleted artikal: " + artikal.id);
                  
                    artikal.aktivan = false;
                }

            },
            function(error) {
                console.log(error);
            }


        );

    };

    $scope.getRestoranById = function(id) {
        restoranFactory.getRestoranById(id).then(
    
            function(response) {
                if (response.status == 200) {
                    $scope.restoran = response.data;
                   
                    if($scope.isKupac) {
                        checkFavRestorani(); 
                    }
                   
                }
            }, 
            function (error) {
                console.log(error);
            }

        );

    }; 

    
    $scope.dodajArtikal = function(artikal) {
        
        if (artikal.porcija == undefined) {
            return;
        }
        
        var stavka = {};
        stavka.artikal = artikal;
        stavka.brojPorcija = artikal.porcija;
        
        // za bekend
        var forPut = { username: $scope.user.username, artikalId: stavka.artikal.id, porcija: stavka.brojPorcija};
        $scope.putTrenutneStavke(forPut);
       
        pushStavka(stavka);
        
        
    }

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
        } else if (userService.activeUser.role == -1) {
            $scope.isLoggedIn = false;
        }

        return userService.activeUser.role;
    };

    $scope.putTrenutneStavke = function(stavkaForAdd) {
        korpaService.putTrenutneStavke(stavkaForAdd).then(
            function(response) {
                console.log("Server: put");
            }
            , function(error) {
                console.log(error);
            }
        )
    }

    var pushStavka = function(stavka) {

        if (userService.activeUser.stavke == null) {
            userService.activeUser.stavke = [];
        } else {
            for (var i = 0; i < userService.activeUser.stavke.length; i++) {
                if (userService.activeUser.stavke[i].artikal.id === stavka.artikal.id) {
                    userService.activeUser.stavke[i].brojPorcija += stavka.brojPorcija;
                    userService.getTotal();
                    return;
                }
            }

        }
        
        userService.activeUser.stavke.push(stavka);
        userService.getTotal();
                    
    }

    var checkFavRestorani = function() {
        if (userService.activeUser.favRestorani)

        for (var i = 0; i < userService.activeUser.favRestorani.length; i++) {
            if (userService.activeUser.favRestorani[i].id == $scope.restoran.id) {
                $scope.isFavRestoran = true;
            }
        }
    };

    $scope.addToFavRestorani = function(restoran) {
        
        userService.activeUser.favRestorani.push(restoran);
        restoranFactory.addToFavRestorani(restoran.id, userService.activeUser.username).then(
            function(response) {
                if (response.status == 200) {
                    console.log("success");
                    $scope.isFavRestoran = true;

                }
            }, function(error) {
                console.log(error);
            }
        );

        

    };
    $scope.removeFromFavRestorani = function(restoran) {

        for (var i = 0; i < userService.activeUser.favRestorani.length; i++) {
            if (userService.activeUser.favRestorani[i].id = restoran.id) {
                userService.activeUser.favRestorani.splice(i, 1);
            }
        }


        restoranFactory.removeFromFavRestorani(restoran.id, userService.activeUser.username).then(
            function(response) {
                if (response.status == 200) {
                    console.log("success");
                    $scope.isFavRestoran = false;
                }
            }, function(error) {
                console.log(error);
            }

        );
    };

    var getRestoranList = function() {
        restoranFactory.getRestoranList().then(
            function(response) {
                if(response.status == 200) {
                    $scope.restorani = response.data;
                }
            }, function(error) {
                console.log(error);

            }

        );
    }



    init();







}]);