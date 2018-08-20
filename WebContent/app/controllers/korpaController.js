clickEat.controller('korpaController', ['$scope', '$timeout', '$location', 'userService', 'korpaService', 'restoranFactory', function($scope, $timeout, $location, userService, korpaService, restoranFactory) {

    var init = function() {
        $scope.loading = true;

        $scope.blur = false;
        $scope.isAdmin = false;
        $scope.isKupac = false;
        $scope.isDostavljac = false;
        $scope.isLoggedIn = false;
        $scope.expandKorpa = korpaService.expandKorpa;
        var roleInit = checkRole();
        
        if (roleInit == -2 ) {
            $scope.getUserOnSession();
            var callback  = function(){
                if (checkRole() != 0) {
                    console.log("Setting stavke to []");
                    $scope.user = { stavke: []};
                    return;
                } else {
                    
                    $scope.getRestoranMap();
                    $scope.getTrenutneStavke();
                    $scope.user = userService.activeUser;
                    $scope.loading = false;
    
                    
                }
    
            }
            $timeout(callback, 500);
        } 
        else if (roleInit == 0) {
            $scope.getRestoranMap();
            $scope.getTrenutneStavke();
            $scope.user = userService.activeUser;
            $scope.loading = false;

        } else {
            console.log("Setting stavke to []");
            $scope.user = { stavke: []};
            $scope.loading = false;
            return;
        }


        
        
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
        } else if (userService.activeUser.role == -1) {
            $scope.isLoggedIn = false;
        }
        console.log("CheckRole(): " + userService.activeUser.role);
        return userService.activeUser.role;
    };

    $scope.getUserOnSession = function() {
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

    $scope.getTrenutneStavke = function() {
        korpaService.getTrenutneStavkeById(userService.activeUser.username).then(
            function(response) {
                if (response.status == 200) {
                    userService.activeUser.stavke = response.data;
                    userService.getTotal();
                } else {
                    userService.activeUser.stavke = [];
                    userService.activeUser.ukupnaCena = 0;
                }
            }, 
            function(error) {
                userService.activeUser.stavke = [];
                userService.activeUser.ukupnaCena = 0;
                
            }

        );
    }

   

    $scope.expandAndToggleKorpaDiv = function() {
        $scope.expandKorpa = !$scope.expandKorpa;
        korpaService.expandKorpa = !korpaService.expandKorpa;
    }

    $scope.getRestoranMap = function() {
        restoranFactory.getRestoranList().then(
            function(response) {
                if (response.status == 200) {
                    $scope.restoranMap = response.data;
                    console.log($scope.restoranMap[3]);
                }

                else {
                    $scope.restoranMap = [];
                }
            }, function(error) {
                
                $scope.restoranMap = [];
            }
        )
    }
    
    $scope.checkout = function() {
        $location.path("/korpa");    
    }

    $scope.napraviPorudzbinu = function() {
        $scope.blur = true;
    }

    $scope.odustani = function() {
        $scope.blur = false;
    }

    $scope.zavrsiPorudzbinu = function() {

        var d = new Date();
        var datum = d.getDate() + '/' + (d.getMonth()+1) + '/' + d.getFullYear(); 

        var stavkeForPorudzbina = formStavkeForPorudzbina();
        // ptp - porudzbinaToProcess
        var ptp = { kupacUsername: userService.activeUser.username, 
                    ukupnaCena: userService.activeUser.ukupnaCena,
                    datum: datum,
                    napomena: $scope.napomena,
                    stavke: stavkeForPorudzbina
        }

        makeOrder(ptp);

        
        $scope.blur = false;

        

           
    }

    var formStavkeForPorudzbina = function() {
        var stavke = [];
        var stavkeTemp = userService.activeUser.stavke;
        for (var i = 0; i < stavkeTemp.length; i++) {
            var stavka = { artikalId: stavkeTemp[i].artikal.id, brojPorcija: stavkeTemp[i].brojPorcija };
            stavke.push(stavka);
        }

        return stavke;

    }
    
    var makeOrder = function(ptp) {
        korpaService.makeOrder(ptp).then(
            function(response) {
                if (response.status == 200) {
                    console.log("success");
                    clearTrenutneStavke();
                    $location.path('/profile');
                    $location.hash(userService.activeUser.username);
                }
            }, function(error) {
                console.log(error);
            }
        );

    }

    var clearTrenutneStavke = function() {
        userService.activeUser.stavke = [];
        korpaService.clearTrenutneStavke();
    }

    $scope.removeStavka = function(stavka) {
        var stavke = $scope.user.stavke;
      
        for(var i = 0; i < $scope.user.stavke.length; i++) {
            if ($scope.user.stavke[i] === stavka) {
                console.log("Stavka for removal found.");
                userService.activeUser.stavke.splice(i,1);
                userService.getTotal();
                removeFromTrenutneStavke(stavka);
            }
        }
    }

    var removeFromTrenutneStavke = function(stavka) {
        var sfr = { username: userService.activeUser.username,
                    artikalId: stavka.artikal.id,
                    porcija: stavka.brojPorcija
        };

        korpaService.removeFromTrenutneStavke(sfr).then(
            function(response) {
                if (response.status == 200) {
                    console.log("success");
                }

            }, function(error) {
                console.log(error);
            }
        );



    }

    init();

}]);