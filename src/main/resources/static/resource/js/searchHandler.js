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
}