// searchHandler.js
$(document).ready(function() {
    $('#searchForm').on('submit', function(e) {
        e.preventDefault();
        var query = $('#searchQuery').val();
        if (query) {
            // vWorld 지오코딩 API 사용
            $.ajax({
                url: 'https://api.vworld.kr/req/address',
                data: {
                    service: 'address',
                    request: 'getcoord',
                    version: '2.0',
                    crs: 'EPSG:4326',
                    address: query,
                    key: 'F320E4DD-72C4-3869-9343-68E55DB54345'
                },
                success: function(response) {
                    if (response.response.status == 'OK') {
                        var x = response.response.result.point.x;
                        var y = response.response.result.point.y;
                        // 좌표를 사용하여 카카오맵 업데이트 및 주변 매물 갱신
                        updateMap(y, x);
                        fetchProperties(y, x);
                    } else {
                        alert('Geocoding failed: ' + response.response.status);
                    }
                }
            });
        }
    });
});

function updateMap(lat, lng) {
    // 지도 중심을 새로운 좌표로 이동
    var newCenter = new kakao.maps.LatLng(lat, lng);
    map.setCenter(newCenter);

    // 기존 마커 제거
    if (window.currentMarker) {
        window.currentMarker.setMap(null);
    }

    // 새로운 마커 생성 및 지도에 설정
    window.currentMarker = new kakao.maps.Marker({
        position: newCenter
    });
    window.currentMarker.setMap(map);

    // 지도를 이동한 후 마커 업데이트
    updateMarkers();
}

function fetchProperties(lat, lng) {
    $.ajax({
        url: '/properties/within',
        data: {
            southWestLat: lat - 0.05, // 임의로 남서쪽 좌표를 설정 (적절한 범위를 설정 필요)
            southWestLng: lng - 0.05,
            northEastLat: lat + 0.05, // 임의로 북동쪽 좌표를 설정 (적절한 범위를 설정 필요)
            northEastLng: lng + 0.05
        },
        success: function(properties) {
            updateList(properties);
        }
    });
}

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
    document.getElementById('modalDetailPrice').innerText = '관리비: ' + (property.maintenanceFee || "No Maintenance Fee");

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
