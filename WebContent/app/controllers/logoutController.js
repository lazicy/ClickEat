clickEat.controller('logoutController',function($scope, $location, userService){

    userService.logout().then(
        
        function(response) {
            userService.activeUser = { firstname: 'Guest', role: -1};
            $location.path('/');
        }, 

        function (error) {
            console.log(error);
        }



    );


});