clickEat.service('userService',function ($http) {
   
    var self = this;
    // admin api
    this.getUserlist = function() {
        return $http({  method:'GET', 
                        url:'rest/users/getUserlist' });
    };

    this.getUserMap = function(username, password) {
        return $http({  method:'GET', 
                        url:'rest/users/getUsermap/' + username + "/" + password });
    }

    this.loadUserlist = function() {
        self.getUserlist().then(
            function(response) {
                if (response.status == 200) {
                    self.userlist = response.data;
                } else {
                    self.userlist = [];
                }
            }, function (error) {
                self.userlist = [];
            }
        );
    }

    // needs to be replaced, not safe 
    this.getByUsername = function(username) {
        return $http({  method:'GET',
                        url:'rest/users/getByUsername/' + username });
    };
  
    this.register = function(userToRegistrate) {
        return $http({  method:'POST', 
                        url:'rest/users/register', 
                        data: userToRegistrate });
    };

    this.login = function(userToLogin) {
        return $http({  method: 'POST', 
                        url:'rest/users/login', 
                        data: userToLogin });

    };

    this.authenticateAdmin = function(userToAuthenticate) {
        return $http({  method: 'POST', 
                        url:'rest/users/authenticateAdmin', 
                        data: userToAuthenticate });

    };

    this.logout = function() {
        return $http({  method: 'GET',
                        url: 'rest/users/logout'    });
    }

    this.getUserOnSession = function() {
        return $http({  method: 'GET', 
                        url:'rest/users/getUserOnSession' });
    };

    this.deleteUser = function(username) {
        return $http({  method:'DELETE',
                        url:'rest/users/deleteUser/' + username});
    };

    this.loadUserlist = function() {
        self.getUserlist().then(
            function(response) {
                if (response.status == 200) {
                    self.userlist = response.data;
                } else {
                    self.userlist = [];
                }
            }, function (error) {
                self.userlist = [];
            }
        );
    }

    this.getDistinctCheck = function(username, email) {
        return $http({  method:'GET',
                        url:'rest/users/getDistinctCheck' + "/" + username + "/" + email  });
    };

    this.modifyUser = function(utm) {
        return $http({ method:'POST',
                        url:'rest/users/modifyUser',
                        data: utm });
    };

    this.zaduziVozilo = function(username, voziloId) {
        return $http({  method:'GET',
                        url:'rest/users/zaduziVozilo/' + username + '/' + voziloId});
    };

    this.razduziVozilo = function(username, voziloId) {
        return $http({  method:'GET',
                        url:'rest/users/razduziVozilo/' + username + '/' + voziloId});
    };

    this.preuzmiPorudzbinu = function(username, porudzbinaId) {
        return $http({  method:'GET',
                        url:'rest/users/preuzmiPorudzbinu/' + username + "/" + porudzbinaId});
    };

    this.userlist = [];
    this.activeUser = { role: -2 };
    

    this.getTotal = function() {
        var total = 0;
        for(var i = 0; i < self.activeUser.stavke.length; i++) {
            total += (self.activeUser.stavke[i].artikal.cena * self.activeUser.stavke[i].brojPorcija);
        }
        
        self.activeUser.ukupnaCena = total;

    }



    
    



});