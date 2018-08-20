clickEat.controller('userController', function($scope, userService) {

    function init() {
        console.log('UserController.Init');
        userService.getUserlist().then(
            
            function (response) {
                $scope.userlist = response.data;
            },
            
            function (error) {
                console.log(error);
            }
        
        );
    }

    init();

 

});

