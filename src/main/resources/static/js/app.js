// src/main/resources/static/js/app.js
const map = L.map('map').setView([20.98892, 105.94601], 14);
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
  attribution: '&copy; OpenStreetMap contributors'
}).addTo(map);

let routingControl;

document.getElementById('restaurantList').addEventListener('click', e => {
  if (!e.target.dataset.key) return;
  const dest = e.target.dataset.key;
  fetch(`/api/route?from=VinUniversity&to=${dest}`)
    .then(r => r.json())
    .then(path => {
      if (routingControl) map.removeControl(routingControl);
      const waypoints = path.map(p => L.latLng(p.lat, p.lon));
      routingControl = L.Routing.control({
        waypoints,
        routeWhileDragging: false,
        createMarker: (i, wp) => L.marker(wp.latLng)
      }).addTo(map);
    });
});