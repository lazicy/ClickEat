clickEat.controller('profileController', ['$scope', '$location', '$timeout', '$window', 'userService', 'korpaService', 'restoranFactory', 'vozilaService', function($scope, $location, $timeout, $window, userService, korpaService, restoranFactory, vozilaService) {

    var init = function() {

        $scope.expandUserInfo = true;
        $scope.expandPorudzbineInfo = true;
        $scope.expandStavka = [];
        $scope.modify = [];
        $scope.modifyUser = { porudzbine: [] };
            
        $scope.backUp = [];

        $scope.loading = true;
        $scope.isAdmin = false;
        $scope.isKupac = false;
        $scope.isKupacView = false;
        $scope.isDostavljac = false;
        $scope.isLoggedIn = false;
        $scope.selfDistruction = false;

        $scope.isRightOne = false;

        $scope.brojKolonaPorudzbina = 6;
        
        $scope.izmenaKorisnika = false;
        $scope.distinctUsername = true;
        $scope.distinctEmail = true;
        $scope.phoneValid = true;
        $scope.izmena = {};

        
        var roleInit = checkRole();
        
        if (roleInit == -2 ) {
            getUserOnSession();
           
            var callback  = function(){ 
                var role = checkRole();
                if (role == 0 || role == 1) {
                    checkUser();
                } else if (role == -1) {
                    $location.path("/");
                    $location.hash("");
                    return;
                } else if (role == 2) {
                    adminCheckUser();
                    loadStuff();
                }
                $scope.loading = false;
            }
            $timeout(callback, 1500);
            return;
        } else if ( roleInit == 0 || roleInit == 1) {
            checkUser();
        } else if ( roleInit == -1 ) {
            $location.path("/");
            $location.hash("");
        } else if (roleInit == 2) {
            adminCheckUser();
            loadStuff();
        }

        $scope.loading = false;
        

      
    } 

    

    var checkRole = function() {
        if (userService.activeUser.role == 2) {
            $scope.isAdmin = true;
            $scope.isLoggedIn = true;
            $scope.brojKolonaPorudzbina = 8;
            $scope.isKupacView = false;
        
        } else if (userService.activeUser.role == 0) {
            $scope.isKupac = true;
            $scope.isKupacView = true;
            $scope.isLoggedIn = true;
            $scope.brojKolonaPorudzbina = 6;
            
        } else if (userService.activeUser.role == 1) {
            $scope.isDostavljac = true;
            $scope.isLoggedIn = true;
            
            $scope.isKupacView = true;
        
        } else if (userService.activeUser.role == -1) {
            $scope.isLoggedIn = false;
        }
        console.log("CheckRole(): " + userService.activeUser.role);
        return userService.activeUser.role;
    };

    var checkUser = function() {
        if ($location.hash() === "") {
            $scope.isRightOne = true;
            $scope.user = userService.activeUser;
           console.log("Default path for profile. Right user.")
            return true;
        } else {

            
            if (userService.activeUser.username === $location.hash()) {
                $scope.isRightOne = true;
                $scope.user = userService.activeUser;
                console.log("Right user");
                return true;
            } else {
                $scope.isRightOne = false;
                $location.path("/");
                $location.hash("")
                return false;
            }
        }
    }
    
    var getTargetUser = function(hash) {
        console.log("getTargetUser() triggered")
        userService.getByUsername(hash).then(
            function(response) {
                if (response.status == 200) {
                    $scope.user = response.data;
                    $scope.isKupacView = true;
                   // initializeExpandStavke()
           
                    console.log("User found: " + $scope.user);
                } else {
                    console.log("No user found.");
                }
            }, function(error) {
                console.log("No user found error.")

            }
        );
    }

    var adminCheckUser = function() {
        var hash = $location.hash();
        if (hash === "" || hash === userService.activeUser.username) {
            console.log("Hash: " + hash);
            $scope.isRightOne = true;
            $scope.user = userService.activeUser;
            $scope.selfDistruction = true;
            console.log("Default path for profile. Right user.")
            return true;
        } else {
            $scope.selfDistruction = false;
            getTargetUser(hash);

        }
    } 

    var loadStuff = function() {
        restoranFactory.loadRestoranList();
    }

    var getUserOnSession = function() {
        userService.getUserOnSession().then(
            
            function(response) {
                if (response.status == 200 ) {

                    userService.activeUser = response.data;
                    console.log("AFTER getUserOnSession(): userService.activeUser = " + userService.activeUser.username);
        

                } else {
                    console.log("No user on session.");
                    userService.activeUser = { lastname: 'Guest', role: -1};
                   
                   
                }
            }, 

            function (error) {
                userService.activeUser = { lastname: 'Guest', role: -1};
               
                console.log(error);
            }

        );
    }

    $scope.roles = {    0: "Kupac",
                        1: "Dostavljač",
                        2: "Administrator"
    };
    
    $scope.statusiPorudzbine = {    1: "Poručeno",
                                    2: "Dostava u toku",
                                    3: "Otkazano",
                                    4: "Dostavljeno"

    };
    
    $scope.expandOrToggleUserInfo = function() {
        $scope.expandUserInfo = !$scope.expandUserInfo;
    };

    $scope.expandOrTogglePorudzbineInfo = function() {
        $scope.expandPorudzbineInfo = !$scope.expandPorudzbineInfo;
    };

    var initializeExpandStavke = function() {
        for (var i = 0; i < $scope.user.porudzbine.length; i++) {
            
            var i = $scope.user.porudzbine[i].idPorudzbine;
            $scope.expandStavka.push({ i : false});
        }

        console.log($scope.expandStavka);
    }

    $scope.expandOrToggleStavke = function(i) {
        $scope.expandStavka[i] = !$scope.expandStavka[i];

    }

    $scope.modifyPorudzbina = function(i) {
        $scope.modify[i] = !$scope.modify[i];

        if ($scope.modify[i]) {
            $scope.expandStavka[i] = true;
            $scope.backUp[i] = [];
            var porudzbine = $scope.user.porudzbine;
            for (var j = 0; j < porudzbine.length; j++) {
                // polja koja mi trebaju: statusPorudzbine [zaSada]
                if (porudzbine[j].idPorudzbine == i) {
                    $scope.modifyUser.porudzbine[i] = {
                                                        statusPorudzbine: porudzbine[j].statusPorudzbine,
                                                        napomena: porudzbine[j].napomena
                    };
                    $scope.modifyUser.porudzbine[i].strStatusPorudzbine = String(porudzbine[j].statusPorudzbine);
                    console.log("Status porudzbine:" + $scope.modifyUser.porudzbine[i].strStatusPorudzbine);
                } 

                
            }

        }
        
    }


    $scope.removeStavka = function(stavka, porudzbina) {
        var backUpStavke = [];
        for(var i = 0; i < porudzbina.stavkePorudzbine.length; i++) {
            if (porudzbina.stavkePorudzbine[i] === stavka) {
                console.log("Stavka for removal found.");
                porudzbina.stavkePorudzbine.splice(i,1);
                $scope.backUp[porudzbina.idPorudzbine].push(stavka);
            }
        }

        porudzbina.ukupnaCena = calculateUkupnaCena(porudzbina.stavkePorudzbine);
    }
    

    $scope.sacuvajIzmene = function(idPorudzbine) {
        var porudzbine = $scope.user.porudzbine;
        var porudzbina = {};
        for (var j = 0; j < porudzbine.length; j++) {
            if (porudzbine[j].idPorudzbine == idPorudzbine) {
                porudzbina = porudzbine[j];
                porudzbine[j].napomena = $scope.modifyUser.porudzbine[idPorudzbine].napomena;
                porudzbine[j].statusPorudzbine = parseInt($scope.modifyUser.porudzbine[idPorudzbine].strStatusPorudzbine);
                console.log("Status porudzbine user: " + porudzbine[j].statusPorudzbine);
                $scope.modify[idPorudzbine] = false;
            } 
        }

        $scope.backUp[idPorudzbine] = [];
        modifyPorudzbina(formPorudzbinaToModify(porudzbina));

    }


    $scope.odustaniIzmene = function(idPorudzbine) {
        $scope.modify[idPorudzbine] = false;
        var porudzbine = $scope.user.porudzbine;
        for (var j = 0; j < porudzbine.length; j++) {
            if (porudzbine[j].idPorudzbine == idPorudzbine) {
                var porudzbina = porudzbine[j];
                for (var i = 0; i < $scope.backUp[idPorudzbine].length; i++) {
                    porudzbine[j].stavkePorudzbine.push($scope.backUp[idPorudzbine][i]);
                }

                porudzbine[j].ukupnaCena = calculateUkupnaCena(porudzbine[j].stavkePorudzbine);
            } 
        }

        
        $scope.backUp[idPorudzbine] = [];

    }
   
    $scope.deletePorudzbina = function(idPorudzbine) {



        korpaService.deletePorudzbina(idPorudzbine).then(
            function(response) {
                if (response.status == 200) {
                    console.log("success");
                    setToFalse(idPorudzbine);
                    $scope.modify[idPorudzbine] = false;
                }
            }, function(error) {
                    console.log(error);

            }
        )
    }

    var setToFalse = function(idPorudzbine) {

        for (var i = 0; i < $scope.user.porudzbine.length; i++) {
            if ($scope.user.porudzbine[i].idPorudzbine === idPorudzbine) {
                $scope.user.porudzbine[i].aktivna = false;
            }
        }
    }

    var calculateUkupnaCena = function(stavke) {
        var ukupnaCena = 0;

        for (var i = 0; i < stavke.length; i++) {
            ukupnaCena += (stavke[i].artikal.cena * stavke[i].brojPorcija);
        }

        return ukupnaCena;
    }

    var formPorudzbinaToModify = function(porudzbina) {
        var sfp = formStavkeForPorudzbina(porudzbina.stavkePorudzbine);

        // porudzbina to modify
        var ptm = { idPorudzbine: porudzbina.idPorudzbine,
                    kupacUsername: porudzbina.kupacUsername,
                    dostavljacUsername: porudzbina.dostavljacUsername,
                    ukupnaCena: porudzbina.ukupnaCena,
                    datum: porudzbina.datum,
                    statusPorudzbine: porudzbina.statusPorudzbine,
                    napomena: porudzbina.napomena,
                    stavke: sfp
       };

       return ptm;

    }

    var formStavkeForPorudzbina = function(stavkeTemp) {
        var stavke = [];
        
        for (var i = 0; i < stavkeTemp.length; i++) {
            var stavka = { artikalId: stavkeTemp[i].artikal.id, brojPorcija: stavkeTemp[i].brojPorcija };
            stavke.push(stavka);
        }

        return stavke;

    }

    var modifyPorudzbina = function(ptm) {
        korpaService.modifyPorudzbina(ptm).then(
            function(response) {
                if (response.status == 200) {
                    console.log("success");
                }
            }, function(error) {
                console.log(error);

            }
        );
    }

    $scope.deleteUser = function(username) {
        userService.deleteUser(username).then(
            function(response) {
                if (response.status == 200) {
                    console.log("success");
                    $location.path("/");
                    $scope.hash("");
                }
            }, function(error) {
                console.log(error);
            }

        );
    }

    $scope.modifyKorisnik = function() {
        $scope.izmenaKorisnika = true;
        deepCopyIzmena();
    };

    $scope.cancelModify = function() {
        $scope.izmenaKorisnika = false;
        $scope.izmena = {};
    };

    $scope.saveModifiedUser = function() {
       
        makeUserToModify();
    };

    var makeUserToModify = function() {
        var roleParsed = parseInt($scope.roleStr);

        var utm = {
            oldUsername: $scope.user.username,
            newUsername: $scope.izmena.username,
            password: $scope.izmena.password,
            firstname: $scope.izmena.firstname,
            lastname: $scope.izmena.lastname,
            role: roleParsed,
            phone: $scope.izmena.phone,
            email: $scope.izmena.email,
            registrationDate: $scope.user.registrationDate,
        };

        if (roleParsed == 0) {
            utm.bodovi = $scope.izmena.bodovi;
        } else {
            utm.bodovi = 0;
        }

        userService.modifyUser(utm).then(
            function(response) {
                if (response.status == 200) {
                    console.log("unbeliveable success");
                    $scope.izmenaKorisnika = false;
                    $location.path("/profile");
                    $location.hash($scope.izmena.username);
                    $window.location.reload();
                } 
            }, function(error) {

                $scope.distinctUsername = false;

            }
        );
    }

    var deepCopyIzmena = function() {
        $scope.izmena.username = $scope.user.username;
        $scope.izmena.password = $scope.user.password;
        $scope.izmena.firstname = $scope.user.firstname;
        $scope.izmena.lastname = $scope.user.lastname;
        $scope.roleStr = String($scope.user.role);
        $scope.izmena.phone = $scope.user.phone;
        $scope.izmena.email = $scope.user.email;
        if ($scope.user.role == 0) {
            $scope.izmena.bodovi = $scope.user.bodovi;
        }
    };

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

   


    init();
}]);