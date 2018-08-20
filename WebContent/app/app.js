var clickEat = angular.module('clickEat', ['ngRoute']);



clickEat.config(function($routeProvider) {
	$routeProvider.when('/',
	{
		controller: 'mainController',
		templateUrl: 'partials/main.html'
	})

	.when('/users', {
		controller: 'userController',
		templateUrl: 'partials/userlist.html'
	})

	.when('/registration',
	{
		controller: 'registrationController',
		templateUrl: 'partials/registration.html'
	})

	.when('/login',
	{
		controller: 'loginController',
		templateUrl: 'partials/login.html'
	})

	.when('/logout',
	{
		controller: 'logoutController',
		templateUrl: 'partials/logout.html'
	})

	.when('/restoran',
	{
		controller: 'restoranController',
		templateUrl: 'partials/restoran.html'
	})
	.when('/restoranmanipulation',
	{
		controller: 'restoranManipulationController',
		templateUrl: 'partials/restoran_manipulation.html'
	})
	.when('/artikalmanipulation',
	{
		controller: 'artikalManipulationController',
		templateUrl: 'partials/artikal_manipulation.html'
	})
	.when('/korpa',
	{
		controller: 'korpaController',
		templateUrl: 'partials/korpa_extended.html'
	})
	.when('/profile', 
	{
		controller: 'profileController',
		templateUrl: 'partials/profile.html'
	})
	.when('/liste',
	{
		controller: 'listeController',
		templateUrl: 'partials/liste.html'
	})
	.when('/vozilomanipulation',
	{
		controller: 'voziloManipulationController',
		templateUrl: 'partials/vozilo_manipulation.html'
	})
});

clickEat.config(function($logProvider){
    $logProvider.debugEnabled(true);
});

