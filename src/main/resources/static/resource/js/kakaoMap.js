document.addEventListener('DOMContentLoaded', function() {
    var mapContainer = document.getElementById('map'),
        mapOption = {
            center: new kakao.maps.LatLng(37.5665, 126.9780),
            level: 8
        };

    var map = new kakao.maps.Map(mapContainer, mapOption);

    console.log('Properties Array:', properties); // properties 배열 확인

    var positions = properties
    .filter(function(property) {
        return property.processingStatus === '승인';
    })
    .map(function(property) {
        return {
            id: property.propertyId,
            title: property.propertyNum,
            latlng: new kakao.maps.LatLng(property.latitude, property.longitude),
            address: property.propertyAddress,
            price: property.price,
            propertyImageList: property.propertyImageList || []
        };
    });


    var markers = positions.map(function(position) {
        return new kakao.maps.Marker({
            position: position.latlng,
            title: position.title
        });
    });

    var clusterer = new kakao.maps.MarkerClusterer({
        map: map,
        averageCenter: true,
        minLevel: 1
    });

    clusterer.addMarkers(markers);

    function addPropertiesToList(properties) {
    var listContainer = document.getElementById('list');
    listContainer.innerHTML = ''; // 기존 리스트를 초기화합니다.

    properties
        .filter(function(property) {
            return property.processingStatus === '승인';
        })
        .forEach(function(property) {
            var imageUrl = property.propertyImageList.length > 0 ? "/display?filename=" + property.propertyImageList[0].imageStoredName : "/resource/image/defaultImage.png";

            var listItem = document.createElement('div');
            listItem.className = 'list-group-item';
            listItem.innerHTML = `
                <div class="card mb-3">
                    <img src="${imageUrl}" class="card-img-top" alt="Property Image">
                    <div class="card-body">
                        <h5 class="card-title">매물번호 : ${property.propertyId}</h5>
                        <p class="card-text">건물명: ${property.buildingName}</p>
                        <p class="card-text">주소: ${property.propertyAddress}</p>
                        <p class="card-text">가격: ${property.monthlyRent+" / "+property.deposit}</p>
                    </div>
                </div>`;
            listItem.addEventListener('click', function() {
                showPropertyDetails(property);
            });
            listContainer.appendChild(listItem);
        });
}


    function showPropertyDetails(property) {
        document.getElementById('modalTitle').innerText = '매물번호 : ' +(property.propertyId || "No Title");
        document.getElementById('modalAddress').innerText = '주소: ' + (property.propertyAddress || "No Address");
        document.getElementById('modalPrice').innerText = '가격: ' + (property.monthlyRent+" / "+property.deposit || "No Price");
        document.getElementById('propertyDetailsLink').href = `/property/${property.propertyId}`;

        // Carousel 이미지 추가
        var carouselInner = document.getElementById('carousel-inner');
        carouselInner.innerHTML = ''; // 기존 이미지 초기화

        if (property.propertyImageList && property.propertyImageList.length > 0) {
            property.propertyImageList.forEach((image, index) => {
                var div = document.createElement('div');
                div.className = 'carousel-item' + (index === 0 ? ' active' : '');
                div.innerHTML = `<img src="/resource/image/${image.imageStoredName}" class="d-block w-100" alt="Property Image">`;
                carouselInner.appendChild(div);
            });
        } else {
            var div = document.createElement('div');
            div.className = 'carousel-item active';
            div.innerHTML = `<img src="/resource/image/defaultImage.png" class="d-block w-100" alt="Property Image">`;
            carouselInner.appendChild(div);
        }

        // 상세 정보 추가
        document.getElementById('modalSummary').innerText = property.shortDescription || "No Summary";
        document.getElementById('modalDetailAddress').innerText = '주소: ' + (property.propertyAddress || "No Address");
        document.getElementById('modalDetailSize').innerText = '면적: ' + (property.sizePyeong || "No Size");
        document.getElementById('modalDetailFloor').innerText = '층수: ' + (property.floor || "No Floor");
        document.getElementById('modalDetailParking').innerText = '주차: ' + (property.propertyOption.parking || "No Parking");
        document.getElementById('modalDetailPrice').innerText = '관리비: ' + (property.maintenanceFee || "No Maintenance Fee");
        
        // 입주일 포맷팅
        var availableDate = new Date(property.availableDate);
        var formattedDate = availableDate.toLocaleDateString('ko-KR', { year: 'numeric', month: '2-digit', day: '2-digit' });
        document.getElementById('modalDetailMoveInDate').innerText = '입주일: ' + formattedDate;

        // 옵션 반복문 처리
	    var options = property.propertyOption || [];
	    var heatingSystem = [];
	    var coolingSystem = [];
	    var livingFacilities = [];
	    var securityFacilities = [];
	    var propertyFeatures = [];
	
	    options.forEach(option => {
	        heatingSystem.push(option.heatingSystem || "No Heating");
	        coolingSystem.push(option.coolingSystem || "No Cooling");
	        livingFacilities.push(option.livingFacilities || "No Facilities");
	        securityFacilities.push(option.securityFacilities || "No Security");
	        propertyFeatures.push(option.propertyFeatures || "No Features");
	    });
	
	    document.getElementById('modalOptions').innerText = heatingSystem.join(', ');
	    document.getElementById('modalOptions').innerText = coolingSystem.join(', ');
	    document.getElementById('modalOptions').innerText = livingFacilities.join(', ');
	    document.getElementById('modalSecurity').innerText = securityFacilities.join(', ');
	    document.getElementById('modalCharacteristics').innerText = propertyFeatures.join(', ');

        var modal = new bootstrap.Modal(document.getElementById('propertyModal'));
        modal.show();
    }

    // 초기 상태에서 모든 매물을 리스트에 추가합니다.
    addPropertiesToList(positions);

    kakao.maps.event.addListener(clusterer, 'clusterclick', function(cluster) {
        var markers = cluster.getMarkers();
        var clusterProperties = markers.map(function(marker) {
            return positions.find(function(position) {
                return position.latlng.equals(marker.getPosition());
            });
        });
        addPropertiesToList(clusterProperties);
    });

    function updateMarkers() {
    var bounds = map.getBounds();
    var swLatLng = bounds.getSouthWest();
    var neLatLng = bounds.getNorthEast();

    fetch(`/properties/within?southWestLat=${swLatLng.getLat()}&southWestLng=${swLatLng.getLng()}&northEastLat=${neLatLng.getLat()}&northEastLng=${neLatLng.getLng()}`)
        .then(response => response.json())
        .then(properties => {
            var positions = properties
                .filter(function(property) {
                    return property.processingStatus === '승인';
                })
                .map(property => ({
                    id: property.propertyId,
                    title: property.propertyNum,
                    latlng: new kakao.maps.LatLng(property.latitude, property.longitude),
                    address: property.propertyAddress,
                    price: property.price,
                    propertyImageList: property.propertyImageList || []
                }));

            var markers = positions.map(position => new kakao.maps.Marker({
                position: position.latlng,
                title: position.title
            }));

            clusterer.clear();
            clusterer.addMarkers(markers);

            updateList(properties.filter(function(property) {
                return property.processingStatus === '승인';
            }));
        });
}


    function updateList(properties) {
    var listContainer = document.getElementById('list');
    listContainer.innerHTML = '';

    properties
        .filter(function(property) {
            return property.processingStatus === '승인';
        })
        .forEach(property => {
            var imageUrl = property.propertyImageList.length > 0 ? "/display?filename=" + property.propertyImageList[0].imageStoredName : "/resource/image/defaultImage.png";

            var listItem = document.createElement('div');
            listItem.className = 'list-group-item';
            listItem.innerHTML = `
                <div class="card mb-3">
                    <img src="${imageUrl}" class="card-img-top" alt="Property Image">
                    <div class="card-body">
                        <h5 class="card-title">매물번호 : ${property.propertyId}</h5>
                        <p class="card-text">건물명: ${property.buildingName}</p>
                        <p class="card-text">주소: ${property.propertyAddress}</p>
                        <p class="card-text">가격: ${property.monthlyRent+" / "+property.deposit}</p>
                    </div>
                </div>`;
            listItem.addEventListener('click', function() {
                showPropertyDetails(property);
            });
            listContainer.appendChild(listItem);
        });
}


    updateMarkers();

    kakao.maps.event.addListener(map, 'idle', updateMarkers);

    // 연관 검색어 클릭 시 해당 위치로 이동
    document.getElementById('suggestionsList').addEventListener('click', function(e) {
        if (e.target.tagName === 'LI') {
            var address = e.target.textContent;
            fetch(`/api/vworld/coordinates?address=${address}`)
                .then(response => response.json())
                .then(coords => {
                    var moveLatLon = new kakao.maps.LatLng(coords[0], coords[1]);
                    map.setCenter(moveLatLon);

                    // 해당 위치의 부동산 목록을 가져와 표시
                    fetch(`/properties/within?southWestLat=${coords[0] - 0.01}&southWestLng=${coords[1] - 0.01}&northEastLat=${coords[0] + 0.01}&northEastLng=${coords[1] + 0.01}`)
                        .then(response => response.json())
                        .then(properties => {
                            var positions = properties.map(property => ({
                                id: property.propertyId,
                                title: property.propertyNum,
                                latlng: new kakao.maps.LatLng(property.latitude, property.longitude),
                                address: property.propertyAddress,
                                price: property.price,
                                propertyImageList: property.propertyImageList || []
                            }));

                            var markers = positions.map(position => new kakao.maps.Marker({
                                position: position.latlng,
                                title: position.title
                            }));

                            clusterer.clear();
                            clusterer.addMarkers(markers);
                            updateList(properties);
                        });
                });
        }
    });
});
