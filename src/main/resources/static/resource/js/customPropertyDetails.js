document.addEventListener('DOMContentLoaded', function() {
    console.log('Custom property details script loaded.');

    // 모달 열기
    document.querySelectorAll('.view-options').forEach(button => {
        button.addEventListener('click', function() {
            const options = JSON.parse(this.getAttribute('data-options'));
            let optionsHtml = `
                <p>Heating: ${options.heatingSystem}</p>
                <p>Cooling: ${options.coolingSystem}</p>
                <p>Living Facilities: ${options.livingFacilities}</p>
                <p>Security Facilities: ${options.securityFacilities}</p>
                <p>Other Facilities: ${options.otherFacilities}</p>
                <p>Parking: ${options.parking}</p>
                <p>Elevator: ${options.elevator}</p>
                <p>Features: ${options.propertyFeatures}</p>
            `;
            document.getElementById('modalBody').innerHTML = optionsHtml;
            $('#optionsModal').modal('show');
        });
    });
});
