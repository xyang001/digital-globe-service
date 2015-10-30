'use strict';

L.mapbox.accessToken = 'pk.eyJ1IjoiZGlnaXRhbGdsb2JlIiwiYSI6ImYzOGIxMGFiY2U2OWFhYmY1OTliMWZiNmU0ZDI4YTA2In0.tpI2apP54MEj7xUiuSOWNQ';

// Avoid the page jump due to overflow clipping on the page?
// https://github.com/Leaflet/Leaflet/issues/2463
L.Map.addInitHook(function() {
  return L.DomEvent.off(this._container, 'mousedown', this.keyboard._onMouseDown);
});

var maps = {};

function refreshMaps() {
  for (var key in maps) {
    maps[key].invalidateSize();
  }
}

importData.coverage.forEach(function(region) {
  var parent = d3.select('#js-coverage').append('div');
  parent.attr('class', 'space-bottom1');

  parent
    .append('div')
    .attr('class', 'strong')
    .text(region.section);

  parent
    .selectAll('div')
    .data(region.coverage)
    .enter()
    .append('div')
    .attr('class', 'quiet')
    .text(function(d) {
      return d;
    });
});

maps.one = L.mapbox.map('map-one', 'mapbox.satellite', {
  zoomControl: false,
  attributionControl: false
}).setView({lat: 38.87297, lng: -77.00732}, 16);

maps.two = L.mapbox.map('map-two', 'mapbox.satellite', {
  zoomControl: false,
  attributionControl: false
}).setView([-16.2114, 128.2324], 10);

maps.one.scrollWheelZoom.disable();
maps.one.touchZoom.disable();

maps.two.scrollWheelZoom.disable();
maps.two.touchZoom.disable();

var $vividExamples = d3.select('#vivid-examples');
var $vividCoverage = d3.select('#vivid-coverage');

// Basemap + Vivid toggle
d3.selectAll('[name="vivid-toggle"]').on('change', function() {
  var val = d3.event.target.getAttribute('data-tab');

  $vividExamples.classed('active', function() {
    return $vividExamples.node().id === val;
  });

  $vividCoverage.classed('active', function() {
    return $vividCoverage.node().id === val;
  });

  refreshMaps();
});

// ===================
// Basemap examples
// ===================
var basemapExamples = [
  { title: 'Washington DC', coords: '38.87297,-77.00732', zoom: 16 },
  { title: 'Seattle', coords: '47.6049,-122.3426', zoom: 16 },
  { title: 'Madrid', coords: '40.4,-3.705', zoom: 14 },
  { title: 'Jerusalem', coords: '31.76,35.2', zoom: 15}
];

maps.basemap = L.mapbox.map('basemap', 'mapbox.satellite', {
  zoomControl: false,
  attributionControl: false
}).setView(basemapExamples[0].coords.split(','), basemapExamples[0].zoom);

maps.basemap.scrollWheelZoom.disable();
maps.basemap.touchZoom.disable();

var bm = d3.select('#basemap');
bm.attr('data-examples', JSON.stringify(basemapExamples));

// Set the first example
bm.select('span').text(basemapExamples[0].title);

// ===================
// +Vivid examples
// ===================
var vividExamples = [
  { title: 'Australia Countrywide', coords: '-16.2114,128.2324', zoom: 10 },
  { title: 'Sydney Harbor', coords: '-33.859,151.205', zoom: 15 },
  { title: 'Paris - de Gaulle Airport', coords: '49.01358,2.54466', zoom: 17 },
  { title: 'Cabo San Lucas', coords: '22.8808,-109.9051', zoom: 16}
];

var v = d3.select('#map-vivid');
v.attr('data-examples', JSON.stringify(vividExamples));

// Set the first example
v.select('span').text(vividExamples[0].title);

maps.vivid = L.mapbox.map('map-vivid', 'mapbox.satellite', {
  zoomControl: false,
  attributionControl: false
}).setView(vividExamples[0].coords.split(','), vividExamples[0].zoom);

maps.vivid.scrollWheelZoom.disable();
maps.vivid.touchZoom.disable();

// Coverage map
// Restrict panning to one copy of the world
var southWest = L.latLng(-90, -180),
    northEast = L.latLng(90, 180),
    bounds = L.latLngBounds(southWest, northEast);

maps.coverage = L.mapbox.map('map-coverage', 'tristen.8182c767', {
  zoomControl: false,
  noWrap: true,
  minZoom: 2,
  maxBounds: bounds,
  attributionControl: false
}).setView([14.093957177836236, 3.076171875], 3);

maps.coverage.scrollWheelZoom.disable();
maps.coverage.touchZoom.disable();

//maps.coverage.on('moveend', () => {
  //console.log(maps.coverage.getCenter());
//});

// ===================
// Mapbox.js Examples
// ===================
var apiExamples = [
  { id: 'map-toggling', title: 'Example: Toggling labels'},
  { id: 'map-geojson', title: 'Example: Adding GeoJSON'},
  { id: 'map-swiping', title: 'Example: Swipe between layers'},
  { id: 'map-kml', title: 'Example: Adding KML data'}
];

var examples = d3.select('#js-api-examples');
examples.attr('data-slides', JSON.stringify(apiExamples));

// Set the first example
examples.select('span').text(apiExamples[0].title);
d3.select('#' + apiExamples[0].id).classed('active', true);

// Example: Toggling
// ===================
maps.toggling = L.mapbox.map('map-toggling', 'mapbox.satellite', {
  attributionControl: false
}).setView({lat: 38.87297, lng: -77.00732}, 8);

maps.toggling.scrollWheelZoom.disable();
maps.toggling.touchZoom.disable();

var layers = document.getElementById('menu-ui');
addLayer(L.mapbox.tileLayer('mapbox.streets-satellite'), 'Toggle labels');

function addLayer(layer, name) {
  layer.setZIndex(2).addTo(maps.toggling);

  var button = document.createElement('button');
    button.className = 'button active';
    button.innerHTML = name;

  button.onclick = function() {
    if (maps.toggling.hasLayer(layer)) {
      maps.toggling.removeLayer(layer);
      this.classList.remove('active');
    } else {
      maps.toggling.addLayer(layer);
      this.classList.add('active');
    }
  };

  layers.appendChild(button);
}

// Example: Adding GeoJSON
// ===================
maps.geojsonExample = L.mapbox.map('map-geojson', 'mapbox.satellite', {
  attributionControl: false
}).setView([38.8929, -77.0252], 15);

maps.geojsonExample.scrollWheelZoom.disable();
maps.geojsonExample.touchZoom.disable();

L.mapbox.featureLayer().addTo(maps.geojsonExample).setGeoJSON(importData.geojson);

// Example: Swiping between layers
// ===================
maps.swipe = L.mapbox.map('map-swiping', 'mapbox.satellite', {
  zoomControl: false,
  attributionControl: false
});

maps.swipe.dragging.disable();
maps.swipe.touchZoom.disable();
maps.swipe.doubleClickZoom.disable();
maps.swipe.scrollWheelZoom.disable();

var overlay = L.mapbox.tileLayer('mapbox.outdoors').addTo(maps.swipe);
var range = document.getElementById('js-range');

function clip() {
  var nw = maps.swipe.containerPointToLayerPoint([0, 0]),
    se = maps.swipe.containerPointToLayerPoint(maps.swipe.getSize()),
    clipX = nw.x + (se.x - nw.x) * range.value;

  overlay.getContainer().style.clip = 'rect(' + [nw.y, clipX, se.y, nw.x].join('px,') + 'px)';
}

range['oninput' in range ? 'oninput' : 'onchange'] = clip;
maps.swipe.on('move', clip);
maps.swipe.setView([49.434, -123.272], 7);

// Example: Loading KML data
// ===================
var kmlTheme = L.geoJson(null, {
  style: function() {
    return {
      'color': '#fa946e',
      'opacity': 1,
      'width': 6
    };
  }
});

maps.kml = L.mapbox.map('map-kml', 'mapbox.satellite', {
  attributionControl: false
}).setView([37.82398042492735, -122.3653256893158], 15);

maps.kml.scrollWheelZoom.disable();
maps.kml.touchZoom.disable();

omnivore.kml('/js/kml-data.kml', null, kmlTheme).addTo(maps.kml);

// Paging controls
// ===================
function page(_this, state) {
  var parent = d3.select('#' + _this.getAttribute('data-pager'));
  var slides = JSON.parse(parent.attr('data-slides'));
  var current = parent.select('.tab-content.active');
  var titleCard = parent.select('span');
  var index = 0;

  // Find the current index
  slides.forEach(function(s, i) {
    if (s.id === current.node().id) index = i;
  });

  if (state === 'next') {
    index = ((index + 1) > (slides.length - 1)) ? 0 : index + 1;
  } else {
    index = ((index - 1) < 0) ? slides.length - 1 : index - 1;
  }

  titleCard.text(slides[index].title);
  current.classed('active', false);
  parent.select('#' + slides[index].id).classed('active', true);
  refreshMaps();
}

d3.selectAll('.js-next').on('click', function() {
  d3.event.preventDefault();
  page(this, 'next');
});

d3.selectAll('.js-previous').on('click', function() {
  d3.event.preventDefault();
  page(this, 'previous');
});

// Example switching
// ===================
function example(_this, state) {
  var mapId = _this.getAttribute('data-map');
  var parent = d3.select('#' + _this.getAttribute('data-parent'));

  var examples = JSON.parse(parent.attr('data-examples'));
  var titleCard = parent.select('span');
  var index = 0;

  // Find the current index
  examples.forEach(function(e, i) {
    if (e.title === titleCard.node().textContent) index = i;
  });

  if (state === 'next') {
    index = ((index + 1) > (examples.length - 1)) ? 0 : index + 1;
  } else {
    index = ((index - 1) < 0) ? examples.length - 1 : index - 1;
  }

  titleCard.text(examples[index].title);
  maps[mapId].setView(examples[index].coords.split(','), examples[index].zoom);
}

d3.selectAll('.js-example-next').on('click', function() {
  d3.event.preventDefault();
  example(this, 'next');
});

d3.selectAll('.js-example-previous').on('click', function() {
  d3.event.preventDefault();
  example(this, 'previous');
});

