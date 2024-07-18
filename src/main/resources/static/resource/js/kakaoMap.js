document.addEventListener('DOMContentLoaded', function() {
    var mapContainer = document.getElementById('map'),
        mapOption = {
            center: new kakao.maps.LatLng(37.5665, 126.9780),
            level: 8
        };

    var map = new kakao.maps.Map(mapContainer, mapOption);

    console.log('Properties Array:', properties); // properties 배열 확인

    var positions = properties.map(function(property) {
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

        properties.forEach(function(property) {
            var imageUrl = property.propertyImageList.length > 0 ? "/resource/image/" + property.propertyImageList[0].imageStoredName : "/resource/image/defaultImage.png";

            var listItem = document.createElement('div');
            listItem.className = 'list-group-item';
            listItem.innerHTML = `
                <div class="card mb-3">
                    <img src="${imageUrl}" class="card-img-top" alt="Property Image">
                    <div class="card-body">
                        <h5 class="card-title">${property.title}</h5>
                        <p class="card-text">주소: ${property.address}</p>
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

    updateMarkers();

    kakao.maps.event.addListener(map, 'idle', updateMarkers);
});
