function fetchSuggestions() {
    const query = $('#searchInput').val();
    if (query.length < 2) {
        $('#suggestionsList').empty();
        return;
    }

    $.ajax({
        url: '/suggestions',
        method: 'GET',
        data: { query: query },
        success: function(data) {
            $('#suggestionsList').empty();
            const uniqueSuggestions = new Set(data);
            uniqueSuggestions.forEach(function(suggestion) {
                $('#suggestionsList').append('<li onclick="selectSuggestion(\'' + suggestion + '\')">' + suggestion + '</li>');
            });
        },
        error: function(error) {
            console.error('Error fetching suggestions:', error);
        }
    });
}

function selectSuggestion(suggestion) {
    $('#searchInput').val(suggestion);
    $('#suggestionsList').empty();

    // 좌표를 가져오기 위한 AJAX 호출
    $.ajax({
        url: '/coordinates',
        method: 'GET',
        data: { address: suggestion },
        success: function(coords) {
            if (coords.length === 2) {
                // 좌표를 성공적으로 가져온 경우 지도 페이지로 이동
                var moveLatLon = new kakao.maps.LatLng(coords[0], coords[1]);
                window.parent.map.setCenter(moveLatLon); // 지도의 중심을 검색한 좌표로 이동
                window.parent.updateMarkers(); // 지도에 있는 마커 업데이트
            } else {
                console.error('Invalid coordinates received');
            }
        },
        error: function(error) {
            console.error('Error fetching coordinates:', error);
        }
    });
}
function searchAddress() {
    var address = document.getElementById('address').value;
    fetch(`/getCoord?address=${address}`)
        .then(response => response.json())
        .then(data => {
            if (data.length === 2) {
                var x = data[0];
                var y = data[1];
                // Move map center to the searched coordinates
                map.setCenter(new kakao.maps.LatLng(y, x));
                var marker = new kakao.maps.Marker({
                    position: new kakao.maps.LatLng(y, x),
                    map: map
                });
            } else {
                alert('Coordinates not found');
            }
        })
        .catch(error => console.error('Error:', error));
}