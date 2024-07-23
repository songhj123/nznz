document.addEventListener("DOMContentLoaded", function() {
    // 지도 초기화
    var mapContainer = document.getElementById('map'), // 지도를 표시할 div
        latitude = mapContainer.getAttribute('data-latitude'),
        longitude = mapContainer.getAttribute('data-longitude'),
        mapOption = {
            center: new kakao.maps.LatLng(latitude, longitude), // 지도의 중심좌표
            level: 3 // 지도의 확대 레벨
        };

    var map = new kakao.maps.Map(mapContainer, mapOption); 

    var markerPosition  = new kakao.maps.LatLng(latitude, longitude); 

    var marker = new kakao.maps.Marker({
        position: markerPosition
    });

    marker.setMap(map);

    var places = new kakao.maps.services.Places();
    places.categorySearch('FD6', function(result, status) {
        if (status === kakao.maps.services.Status.OK) {
            for (var i=0; i<result.length; i++) {
                displayMarker(result[i]);
            }
        }
    }, {useMapBounds:true});

    function displayMarker(place) {
        var marker = new kakao.maps.Marker({
            map: map,
            position: new kakao.maps.LatLng(place.y, place.x) 
        });
    }

    // 사용자 정의 캐러셀 초기화
    var mainImage = document.getElementById('mainImage');
    var thumbnails = document.getElementsByClassName('thumbnail');
    var currentIndex = 0;
    
    function updateMainImage(index) {
        mainImage.src = thumbnails[index].src;
        Array.prototype.forEach.call(thumbnails, function(thumb, idx) {
            thumb.classList.remove('active');
            if (idx === index) {
                thumb.classList.add('active');
            }
        });
    }

    Array.prototype.forEach.call(thumbnails, function(thumbnail, index) {
        thumbnail.addEventListener('click', function() {
            currentIndex = index;
            updateMainImage(currentIndex);
        });
    });

    document.getElementById('prevImage').addEventListener('click', function() {
        currentIndex = (currentIndex - 1 + thumbnails.length) % thumbnails.length;
        updateMainImage(currentIndex);
    });

    document.getElementById('nextImage').addEventListener('click', function() {
        currentIndex = (currentIndex + 1) % thumbnails.length;
        updateMainImage(currentIndex);
    });

    // 첫 번째 썸네일을 활성화로 설정
    if (thumbnails.length > 0) {
        updateMainImage(0);
    }

    // 모달 기능
    var modal = document.getElementById("imageModal");
    var modalImg = document.getElementById("img01");
    var captionText = document.getElementById("caption");
    var prevModalImage = document.getElementById("prevModalImage");
    var nextModalImage = document.getElementById("nextModalImage");

    mainImage.addEventListener('click', function() {
        modal.style.display = "block";
        modalImg.src = this.src;
        captionText.innerHTML = this.alt;
    });

    var span = document.getElementsByClassName("close")[0];
    span.onclick = function() { 
        modal.style.display = "none";
    }

    prevModalImage.onclick = function() {
        currentIndex = (currentIndex - 1 + thumbnails.length) % thumbnails.length;
        modalImg.src = thumbnails[currentIndex].src;
        captionText.innerHTML = thumbnails[currentIndex].alt;
    }

    nextModalImage.onclick = function() {
        currentIndex = (currentIndex + 1) % thumbnails.length;
        modalImg.src = thumbnails[currentIndex].src;
        captionText.innerHTML = thumbnails[currentIndex].alt;
    }
});
