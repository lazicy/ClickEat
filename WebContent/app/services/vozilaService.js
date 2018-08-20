clickEat.service('vozilaService', function($http) {
   
   this.getVozilaMap = function() {
        return $http({  method:'GET',
                        url:'rest/vozila/getVozilaMap'});
   };

   this.getVozilaList = function() {
        return $http({  method:'GET',
                        url:'rest/vozila/getVozilaList'});
   };

   this.getVoziloById = function(id) {
       return $http({   method:'GET',
                        url:'rest/vozila/getVoziloById/' + id});
   };

   this.addVozilo = function(vta) {
       return $http({   method:'POST',
                        url:'rest/vozila/addVozilo',
                        data: vta });
   };

   this.removeVozilo = function(id) {
       return $http({   method:'DELETE',
                        url:'rest/vozila/removeVozilo/' + id});
   };

   this.modifyVozilo = function(vtm) {
       return $http({   method:'POST',
                        url:'rest/vozila/modifyVozilo',
                        data: vtm });
   };

});