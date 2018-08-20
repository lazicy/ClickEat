clickEat.controller('restoranManipulationController', ['$scope', '$location', '$timeout', 'restoranFactory', 'userService',
                    function($scope, $location, $timeout, restoranFactory, userService) {
    
    var init = function() {
        $scope.new = false;
        $scope.modify = false;
        $scope.kategorije = {};
        $scope.kategorije.data = [{str:'Domaća kuhinja', id:'1'}, {str:'Roštilj', id:'2'}, {str:'Kineska kuhinja', id:'3'}, 
        {str:'Indijska kuhinja', id:'4'}, {str:'Poslastičarnica', id:'5'}, {str:'Picerija', id:'6'}];
        

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

    

    $scope.noviRestoran = function() {

        $scope.novi.kategorija = parseInt($scope.novi.kategorija);
        
        restoranFactory.noviRestoran($scope.novi).then(
            function(response) {
                if (response.status == 200) {
                    $location.path('/');
                }
            },
            function (error) {
                console.error(error);
            }
        );


    }
    
    $scope.modifyRestoran = function() {
        console.log("Kategorija: " + $scope.selected);
        $scope.izmena.kategorija = parseInt($scope.kategorijaIdStr);
        $scope.izmena.kategorijaStr = $scope.kategorije.data[$scope.izmena.kategorija-1].str;
        console.log($scope.izmena.kategorijaStr);
        restoranFactory.modifyRestoran($scope.izmena).then(
            function(response) {
                if (response.status = 200) {
                    $location.path('/');
                }
            },
            function (error) {
                console.error(error);
            }


        );
    }

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

                restoranFactory.getRestoranById(id).then(
        
                    function(response) {
                        if (response.status == 200) {
                            $scope.izmena = response.data;
                            $scope.kategorijaIdStr = String($scope.izmena.kategorija);
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


        
    }

    init();






}]);