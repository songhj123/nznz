document.addEventListener('DOMContentLoaded', function() {
    var mapContainer = document.getElementById('map'),
        mapOption = {
            center: new kakao.maps.LatLng(37.5665, 126.9780),
            level: 8
        };

    var map = new kakao.maps.Map(mapContainer, mapOption);

    var clusterer = new kakao.maps.MarkerClusterer({
        map: map,
        averageCenter: true,
        minLevel: 1
    });

    function updateMarkers(bounds) {
        var swLatLng = bounds.getSouthWest();
        var neLatLng = bounds.getNorthEast();

        fetch(`/properties/within?southWestLat=${swLatLng.getLat()}&southWestLng=${swLatLng.getLng()}&northEastLat=${neLatLng.getLat()}&northEastLng=${neLatLng.getLng()}`)
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
    }

    function updateList(properties) {
        var listContainer = document.getElementById('list');
        listContainer.innerHTML = '';
        properties.forEach(property => {
            var imageUrl = property.propertyImageList.length > 0 ? "/resource/image/" + property.propertyImageList[0].imageStoredName : "/resource/image/defaultImage.png";

            var listItem = document.createElement('div');
            listItem.className = 'list-group-item';
            listItem.innerHTML = `
                <div class="card mb-3">
                    <img src="${imageUrl}" class="card-img-top" alt="Property Image">
                    <div class="card-body">
                        <h5 class="card-title">${property.propertyNum}</h5>
                        <p class="card-text">주소: ${property.propertyAddress}</p>
                        <p class="card-text">가격: ${property.price}</p>
                    </div>
                </div>`;
            listItem.addEventListener('click', function() {
                showPropertyDetails(property);
            });
            listContainer.appendChild(listItem);
        });
    }

    function showPropertyDetails(property) {
        document.getElementById('modalTitle').innerText = property.propertyNum || "No Title";
        document.getElementById('modalAddress').innerText = '주소: ' + (property.propertyAddress || "No Address");
        document.getElementById('modalPrice').innerText = '가격: ' + (property.price || "No Price");
        document.getElementById('propertyDetailsLink').href = `/property/${property.propertyId}`;

        var carouselInner = document.getElementById('carousel-inner');
        carouselInner.innerHTML = '';

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

        document.getElementById('modalSummary').innerText = property.shortDescription || "No Summary";
        document.getElementById('modalDetailAddress').innerText = '주소: ' + (property.propertyAddress || "No Address");
        document.getElementById('modalDetailSize').innerText = '면적: ' + (property.sizePyeong || "No Size");
        document.getElementById('modalDetailFloor').innerText = '층수: ' + (property.floor || "No Floor");
        document.getElementById('modalDetailParking').innerText = '주차: ' + (property.parking || "No Parking");
        
        var availableDate = new Date(property.availableDate);
        var formattedDate = availableDate.toLocaleDateString('ko-KR', { year: 'numeric', month: '2-digit', day: '2-digit' });
        document.getElementById('modalDetailMoveInDate').innerText = '입주일: ' + formattedDate;

        document.getElementById('modalOptions').innerText = property.options || "No Options";
        document.getElementById('modalSecurity').innerText = property.security || "No Security";
        document.getElementById('modalCharacteristics').innerText = property.characteristics || "No Characteristics";
        document.getElementById('modalNearbyFacilities').innerText = property.nearbyFacilities || "No Nearby Facilities";

        var modal = new bootstrap.Modal(document.getElementById('propertyModal'));
        modal.show();
    }

    kakao.maps.event.addListener(clusterer, 'clusterclick', function(cluster) {
        var markers = cluster.getMarkers();
        var clusterProperties = markers.map(function(marker) {
            return positions.find(function(position) {
                return position.latlng.equals(marker.getPosition());
            });
        });
        addPropertiesToList(clusterProperties);
    });

    function updateMarkersOnIdle() {
        var bounds = map.getBounds();
        updateMarkers(bounds);
    }

    kakao.maps.event.addListener(map, 'idle', updateMarkersOnIdle);

    document.getElementById('suggestionsList').addEventListener('click', function(e) {
        if (e.target.tagName === 'LI') {
            var address = e.target.textContent;
            fetch(`/api/vworld/coordinates?address=${address}`)
                .then(response => response.json())
                .then(coords => {
                    if (coords.length === 2) {
                        var moveLatLon = new kakao.maps.LatLng(coords[0], coords[1]);
                        map.setCenter(moveLatLon);
                        map.setLevel(5); // 레벨을 설정하여 포커스 확대
                        
                        var bounds = new kakao.maps.LatLngBounds(
                            new kakao.maps.LatLng(coords[0] - 0.01, coords[1] - 0.01),
                            new kakao.maps.LatLng(coords[0] + 0.01, coords[1] + 0.01)
                        );
                        updateMarkers(bounds);
                    }
                });
        }
    });

    updateMarkersOnIdle(); // 초기 로딩 시 마커 업데이트
});