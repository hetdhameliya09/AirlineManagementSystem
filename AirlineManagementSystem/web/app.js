/* ==========================================================================
   SKYWINGS AIRLINE MANAGEMENT SYSTEM - INTERACTIVE FRONTEND LOGIC
   ========================================================================== */

// --- INITIAL STATE & MOCK DATABASE ---
const state = {
  theme: localStorage.getItem('skywings_theme') || 'dark',
  currentTab: 'search',
  activeUser: {
    id: 1,
    username: 'het_patel',
    name: 'Het Patel',
    email: 'het@skywings.com',
    phone: '+1 (555) 019-2834',
    passport: 'K9823412',
    role: 'customer' // 'customer' or 'admin'
  },
  flights: [
    { id: 101, flightNumber: 'SW-204', airline: 'SkyWings Lux', origin: 'JFK', originCity: 'New York', destination: 'LHR', destCity: 'London', depTime: '08:30 AM', arrTime: '08:45 PM', duration: '7h 15m', price: 650, seatsAvailable: 42 },
    { id: 102, flightNumber: 'SW-809', airline: 'SkyWings Express', origin: 'JFK', originCity: 'New York', destination: 'DXB', destCity: 'Dubai', depTime: '11:15 AM', arrTime: '07:30 AM+1', duration: '12h 15m', price: 920, seatsAvailable: 18 },
    { id: 103, flightNumber: 'SW-501', airline: 'SkyWings Lux', origin: 'LHR', originCity: 'London', destination: 'HND', destCity: 'Tokyo', depTime: '02:00 PM', arrTime: '10:30 AM+1', duration: '11h 30m', price: 1150, seatsAvailable: 24 },
    { id: 104, flightNumber: 'SW-112', airline: 'Emirates Partner', origin: 'BOM', originCity: 'Mumbai', destination: 'DXB', destCity: 'Dubai', depTime: '04:15 PM', arrTime: '06:00 PM', duration: '3h 15m', price: 320, seatsAvailable: 55 },
    { id: 105, flightNumber: 'SW-770', airline: 'Singapore Partner', origin: 'SFO', originCity: 'San Francisco', destination: 'SIN', destCity: 'Singapore', depTime: '10:45 PM', arrTime: '06:20 AM+2', duration: '16h 35m', price: 1280, seatsAvailable: 12 },
    { id: 106, flightNumber: 'SW-305', airline: 'SkyWings Lux', origin: 'DEL', originCity: 'New Delhi', destination: 'LHR', destCity: 'London', depTime: '01:30 AM', arrTime: '06:15 AM', duration: '9h 15m', price: 780, seatsAvailable: 30 }
  ],
  bookings: [
    { id: 'BK-9021', flightNumber: 'SW-204', route: 'JFK ✈ LHR', date: '2026-09-15', passengerName: 'Het Patel', seatNumber: '12A', classType: 'Business', totalPrice: 650, status: 'CONFIRMED', ticketNumber: 'TKT-8849102' }
  ],
  hotels: [
    { id: 1, name: 'The Ritz London', city: 'London', pricePerNight: 450, rating: 5.0, image: 'https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=400&q=80' },
    { id: 2, name: 'Burj Al Arab Suite', city: 'Dubai', pricePerNight: 950, rating: 5.0, image: 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=400&q=80' },
    { id: 3, name: 'Park Hyatt Tokyo', city: 'Tokyo', pricePerNight: 520, rating: 4.9, image: 'https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=400&q=80' }
  ],
  taxis: [
    { id: 1, driver: 'Michael Vance', vehicle: 'Mercedes S-Class (Black)', city: 'London (LHR)', fare: 85 },
    { id: 2, driver: 'Tariq Al-Mansoor', vehicle: 'Tesla Model X', city: 'Dubai (DXB)', fare: 65 },
    { id: 3, driver: 'Kenji Sato', vehicle: 'Toyota Crown Executive', city: 'Tokyo (HND)', fare: 75 }
  ],
  notifications: [
    { id: 1, title: 'Flight Confirmation', message: 'Your booking BK-9021 for SW-204 is confirmed. Check-in opens 24h prior.', time: '10 mins ago' },
    { id: 2, title: 'Gate Change Alert', message: 'Flight SW-204 departure gate updated to Terminal 4, Gate B22.', time: '1 hour ago' }
  ],
  selectedFlight: null,
  selectedSeat: null,
  selectedClass: 'Economy'
};

// --- DOM ELEMENTS & INITIALIZATION ---
document.addEventListener('DOMContentLoaded', () => {
  initTheme();
  renderApp();
  setupEventListeners();
});

function initTheme() {
  document.documentElement.setAttribute('data-theme', state.theme);
  updateThemeIcon();
}

function updateThemeIcon() {
  const btn = document.getElementById('theme-toggle-btn');
  if (btn) {
    btn.innerHTML = state.theme === 'dark' ? '☀️' : '🌙';
  }
}

function toggleTheme() {
  state.theme = state.theme === 'dark' ? 'light' : 'dark';
  localStorage.setItem('skywings_theme', state.theme);
  initTheme();
}

// --- RENDER APP & NAV TABS ---
function renderApp() {
  const contentArea = document.getElementById('main-content');
  if (!contentArea) return;

  switch (state.currentTab) {
    case 'search':
      contentArea.innerHTML = renderFlightSearchView();
      attachSearchEvents();
      break;
    case 'bookings':
      contentArea.innerHTML = renderMyBookingsView();
      break;
    case 'hotels':
      contentArea.innerHTML = renderHotelsView();
      break;
    case 'taxis':
      contentArea.innerHTML = renderTaxisView();
      break;
    case 'notifications':
      contentArea.innerHTML = renderNotificationsView();
      break;
    case 'admin':
      contentArea.innerHTML = renderAdminDashboardView();
      break;
    default:
      contentArea.innerHTML = renderFlightSearchView();
  }
}

function switchTab(tabName) {
  state.currentTab = tabName;
  document.querySelectorAll('.nav-item').forEach(el => {
    el.classList.toggle('active', el.dataset.tab === tabName);
  });
  renderApp();
}

// --- VIEWS GENERATOR ---

// 1. Flight Search & Results View
function renderFlightSearchView() {
  return `
    <div class="section-header">
      <div>
        <h2 class="section-title">Available Flights</h2>
        <p style="color: var(--text-secondary);">Direct connections & premium long-haul flights</p>
      </div>
      <div style="display: flex; gap: 1rem;">
        <select id="filter-airline" class="form-select" style="width: auto; padding-right: 2rem;">
          <option value="ALL">All Airlines</option>
          <option value="SkyWings Lux">SkyWings Lux</option>
          <option value="SkyWings Express">SkyWings Express</option>
          <option value="Emirates Partner">Emirates Partner</option>
        </select>
      </div>
    </div>
    
    <div class="results-container" id="flight-results-list">
      ${renderFlightCards(state.flights)}
    </div>
  `;
}

function renderFlightCards(flightsList) {
  if (flightsList.length === 0) {
    return `<div style="text-align: center; padding: 4rem; background: var(--bg-card); border-radius: var(--radius-lg); border: 1px solid var(--glass-border);">
      <h3>No flights found for your criteria</h3>
      <p style="color: var(--text-muted); margin-top: 0.5rem;">Try adjusting origin, destination or airline filter.</p>
    </div>`;
  }

  return flightsList.map(f => `
    <div class="flight-card">
      <div class="airline-info">
        <div class="airline-logo">✈</div>
        <div>
          <div class="airline-name">${f.airline}</div>
          <div class="flight-number">${f.flightNumber} • Aircraft Boeing 787</div>
        </div>
      </div>
      
      <div class="flight-route">
        <div class="route-point">
          <div class="route-time">${f.depTime}</div>
          <div class="route-city">${f.origin} (${f.originCity})</div>
        </div>
        <div class="route-line-container">
          <div class="duration-text">${f.duration}</div>
          <div class="route-line"></div>
          <div class="duration-text" style="color: var(--accent-emerald);">Non-Stop</div>
        </div>
        <div class="route-point">
          <div class="route-time">${f.arrTime}</div>
          <div class="route-city">${f.destination} (${f.destCity})</div>
        </div>
      </div>
      
      <div class="flight-price-col">
        <div class="price-amount">$${f.price}</div>
        <div style="font-size: 0.75rem; color: var(--text-muted); mb-1">${f.seatsAvailable} seats remaining</div>
        <button class="btn-book" onclick="openSeatModal(${f.id})">Select Seats & Book</button>
      </div>
    </div>
  `).join('');
}

// 2. My Bookings View
function renderMyBookingsView() {
  return `
    <div class="section-header">
      <h2 class="section-title">My Flight Bookings</h2>
      <p style="color: var(--text-secondary);">Manage tickets, check boarding passes & status</p>
    </div>

    ${state.bookings.length === 0 ? `
      <div style="text-align: center; padding: 4rem; background: var(--bg-card); border-radius: var(--radius-lg);">
        <h3>No active bookings yet</h3>
        <button class="btn-book" style="margin-top: 1rem;" onclick="switchTab('search')">Search Flights Now</button>
      </div>
    ` : state.bookings.map(b => `
      <div class="boarding-pass-card">
        <div class="pass-main">
          <div class="pass-header">
            <div class="pass-brand">SKYWINGS AIRLINES</div>
            <div class="status-badge confirmed">${b.status}</div>
          </div>
          <div class="pass-route">
            <div>
              <div class="detail-label">Passenger</div>
              <div class="pass-code" style="font-size: 1.4rem;">${b.passengerName}</div>
            </div>
            <div>
              <div class="detail-label">Flight</div>
              <div class="pass-code" style="font-size: 1.4rem; color: var(--accent-blue);">${b.flightNumber}</div>
            </div>
          </div>
          <div class="pass-details-grid">
            <div>
              <div class="detail-label">Date</div>
              <div class="detail-val">${b.date}</div>
            </div>
            <div>
              <div class="detail-label">Seat</div>
              <div class="detail-val" style="color: var(--accent-cyan);">${b.seatNumber}</div>
            </div>
            <div>
              <div class="detail-label">Class</div>
              <div class="detail-val">${b.classType}</div>
            </div>
            <div>
              <div class="detail-label">Gate / Terminal</div>
              <div class="detail-val">B22 / T4</div>
            </div>
          </div>
          <div class="barcode-area">
            <div>
              <div class="detail-label">Ticket Number</div>
              <div style="font-family: monospace; font-weight: 700; color: #475569;">${b.ticketNumber}</div>
            </div>
            <svg class="barcode-svg" viewBox="0 0 100 30">
              <rect x="0" y="0" width="3" height="30" fill="#0f172a"/>
              <rect x="5" y="0" width="1" height="30" fill="#0f172a"/>
              <rect x="8" y="0" width="4" height="30" fill="#0f172a"/>
              <rect x="15" y="0" width="2" height="30" fill="#0f172a"/>
              <rect x="20" y="0" width="5" height="30" fill="#0f172a"/>
              <rect x="28" y="0" width="2" height="30" fill="#0f172a"/>
              <rect x="33" y="0" width="3" height="30" fill="#0f172a"/>
              <rect x="40" y="0" width="1" height="30" fill="#0f172a"/>
              <rect x="44" y="0" width="6" height="30" fill="#0f172a"/>
              <rect x="53" y="0" width="2" height="30" fill="#0f172a"/>
              <rect x="58" y="0" width="4" height="30" fill="#0f172a"/>
              <rect x="65" y="0" width="2" height="30" fill="#0f172a"/>
              <rect x="70" y="0" width="5" height="30" fill="#0f172a"/>
              <rect x="78" y="0" width="3" height="30" fill="#0f172a"/>
              <rect x="84" y="0" width="2" height="30" fill="#0f172a"/>
              <rect x="90" y="0" width="4" height="30" fill="#0f172a"/>
            </svg>
          </div>
        </div>
        <div class="pass-stub">
          <div>
            <div class="detail-label">Boarding Pass</div>
            <div style="font-weight: 800; font-size: 1.1rem; margin-top: 0.2rem;">SKYWINGS</div>
            <div style="font-size: 0.8rem; color: #64748b; margin-top: 1rem;">Boarding Time</div>
            <div style="font-weight: 800; font-size: 1.2rem; color: #0284c7;">45 Mins Before Dep</div>
          </div>
          <button style="background: #0f172a; color: #fff; border: none; padding: 0.75rem; border-radius: 8px; font-weight: 700; cursor: pointer; margin-top: 1rem;" onclick="window.print()">Print Pass 🖨️</button>
        </div>
      </div>
    `).join('')}
  `;
}

// 3. Hotels View
function renderHotelsView() {
  return `
    <div class="section-header">
      <h2 class="section-title">Partner Luxury Hotels</h2>
      <p style="color: var(--text-secondary);">Book stay with your flight for exclusive discounts</p>
    </div>
    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 2rem;">
      ${state.hotels.map(h => `
        <div style="background: var(--bg-card); border: 1px solid var(--glass-border); border-radius: var(--radius-lg); overflow: hidden;">
          <img src="${h.image}" style="width: 100%; height: 180px; object-fit: cover;" alt="${h.name}">
          <div style="padding: 1.5rem;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem;">
              <h3 style="font-size: 1.2rem; font-family: var(--font-heading);">${h.name}</h3>
              <span style="color: var(--accent-gold); font-weight: 700;">★ ${h.rating}</span>
            </div>
            <p style="color: var(--text-muted); font-size: 0.9rem; margin-bottom: 1rem;">📍 ${h.city}</p>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <div>
                <span style="font-size: 1.4rem; font-weight: 800; color: var(--accent-cyan);">$${h.pricePerNight}</span>
                <span style="font-size: 0.8rem; color: var(--text-muted);">/ night</span>
              </div>
              <button class="btn-book" style="padding: 0.5rem 1rem;" onclick="bookHotelModal('${h.name}')">Book Room</button>
            </div>
          </div>
        </div>
      `).join('')}
    </div>
  `;
}

// 4. Taxis View
function renderTaxisView() {
  return `
    <div class="section-header">
      <h2 class="section-title">Airport Executive Transfer</h2>
      <p style="color: var(--text-secondary);">Chauffeur driven rides waiting at your arrival gate</p>
    </div>
    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 1.5rem;">
      ${state.taxis.map(t => `
        <div style="background: var(--bg-card); border: 1px solid var(--glass-border); border-radius: var(--radius-md); padding: 1.5rem; display: flex; justify-content: space-between; align-items: center;">
          <div>
            <h4 style="font-size: 1.1rem; margin-bottom: 0.25rem;">${t.vehicle}</h4>
            <p style="color: var(--text-secondary); font-size: 0.85rem;">Driver: ${t.driver}</p>
            <p style="color: var(--text-muted); font-size: 0.8rem; margin-top: 0.5rem;">📍 ${t.city}</p>
          </div>
          <div style="text-align: right;">
            <div style="font-size: 1.4rem; font-weight: 800; color: var(--accent-cyan); margin-bottom: 0.5rem;">$${t.fare}</div>
            <button class="btn-book" style="padding: 0.4rem 0.9rem; font-size: 0.85rem;" onclick="showToast('Taxi reservation sent to driver!')">Reserve Ride</button>
          </div>
        </div>
      `).join('')}
    </div>
  `;
}

// 5. Notifications View
function renderNotificationsView() {
  return `
    <div class="section-header">
      <h2 class="section-title">Alerts & Notifications</h2>
    </div>
    <div style="display: flex; flex-direction: column; gap: 1rem;">
      ${state.notifications.map(n => `
        <div style="background: var(--bg-card); border: 1px solid var(--glass-border); border-left: 4px solid var(--accent-cyan); border-radius: var(--radius-md); padding: 1.25rem;">
          <div style="display: flex; justify-content: space-between; margin-bottom: 0.5rem;">
            <h4 style="font-weight: 700; color: var(--text-primary);">${n.title}</h4>
            <span style="font-size: 0.8rem; color: var(--text-muted);">${n.time}</span>
          </div>
          <p style="color: var(--text-secondary); font-size: 0.95rem;">${n.message}</p>
        </div>
      `).join('')}
    </div>
  `;
}

// 6. Admin Dashboard View
function renderAdminDashboardView() {
  return `
    <div class="section-header">
      <div>
        <h2 class="section-title">Admin Operations Center</h2>
        <p style="color: var(--text-secondary);">Real-time flight status, fleet analytics & passengers registry</p>
      </div>
      <button class="btn-book" onclick="openAddFlightModal()">+ Add New Flight</button>
    </div>

    <!-- KPI Metrics -->
    <div class="metrics-grid">
      <div class="metric-card">
        <div class="metric-icon">💰</div>
        <div class="metric-info">
          <h4>Total Revenue</h4>
          <div class="metric-val">$148,920</div>
        </div>
      </div>
      <div class="metric-card">
        <div class="metric-icon">✈️</div>
        <div class="metric-info">
          <h4>Active Flights</h4>
          <div class="metric-val">${state.flights.length}</div>
        </div>
      </div>
      <div class="metric-card">
        <div class="metric-icon">🎫</div>
        <div class="metric-info">
          <h4>Total Bookings</h4>
          <div class="metric-val">1,248</div>
        </div>
      </div>
      <div class="metric-card">
        <div class="metric-icon">⭐</div>
        <div class="metric-info">
          <h4>Customer Rating</h4>
          <div class="metric-val">4.92 / 5</div>
        </div>
      </div>
    </div>

    <!-- Active Flights Table -->
    <h3 style="margin-bottom: 1rem; font-family: var(--font-heading);">Fleet & Schedule Overview</h3>
    <div class="table-responsive">
      <table class="custom-table">
        <thead>
          <tr>
            <th>Flight No.</th>
            <th>Airline</th>
            <th>Route</th>
            <th>Departure</th>
            <th>Duration</th>
            <th>Price</th>
            <th>Seats Left</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          ${state.flights.map(f => `
            <tr>
              <td style="font-weight: 700; color: var(--accent-cyan);">${f.flightNumber}</td>
              <td>${f.airline}</td>
              <td>${f.origin} → ${f.destination}</td>
              <td>${f.depTime}</td>
              <td>${f.duration}</td>
              <td style="font-weight: 700;">$${f.price}</td>
              <td>${f.seatsAvailable}</td>
              <td>
                <button style="background: rgba(244, 63, 94, 0.15); border: 1px solid rgba(244, 63, 94, 0.3); color: var(--accent-rose); padding: 0.3rem 0.6rem; border-radius: 6px; cursor: pointer;" onclick="deleteFlight(${f.id})">Delete</button>
              </td>
            </tr>
          `).join('')}
        </tbody>
      </table>
    </div>
  `;
}

// --- INTERACTIVE SEAT MAP MODAL & BOOKING LOGIC ---
function openSeatModal(flightId) {
  const flight = state.flights.find(f => f.id === flightId);
  if (!flight) return;
  state.selectedFlight = flight;
  state.selectedSeat = null;

  const modal = document.getElementById('seat-modal');
  const body = document.getElementById('seat-modal-body');

  body.innerHTML = `
    <h2 style="font-family: var(--font-heading); font-size: 1.8rem; margin-bottom: 0.5rem;">Select Seat for ${flight.flightNumber}</h2>
    <p style="color: var(--text-secondary); margin-bottom: 1.5rem;">Route: ${flight.origin} (${flight.originCity}) ✈ ${flight.destination} (${flight.destCity})</p>

    <!-- Class Selector -->
    <div style="display: flex; gap: 1rem; margin-bottom: 1.5rem;">
      <button class="trip-btn active" id="class-econ" onclick="selectCabinClass('Economy', ${flight.price})">Economy ($${flight.price})</button>
      <button class="trip-btn" id="class-bus" onclick="selectCabinClass('Business', ${Math.round(flight.price * 1.6)})">Business ($${Math.round(flight.price * 1.6)})</button>
      <button class="trip-btn" id="class-first" onclick="selectCabinClass('First Class', ${Math.round(flight.price * 2.5)})">First Class ($${Math.round(flight.price * 2.5)})</button>
    </div>

    <!-- Seat Aircraft Map -->
    <div class="seat-map-container">
      <div class="plane-fuselage">
        <div class="cockpit-view">▲ COCKPIT (FRONT OF AIRCRAFT) ▲</div>

        <div class="cabin-section-title">First Class & Business</div>
        <div class="seat-grid" id="seat-grid-front"></div>

        <div class="cabin-section-title">Main Economy Cabin</div>
        <div class="seat-grid" id="seat-grid-econ"></div>
      </div>
    </div>

    <!-- Booking Summary Panel -->
    <div style="background: var(--bg-card); border: 1px solid var(--glass-border); padding: 1.5rem; border-radius: var(--radius-md); display: flex; justify-content: space-between; align-items: center; margin-top: 1.5rem;">
      <div>
        <div style="font-size: 0.85rem; color: var(--text-muted);">SELECTED SEAT</div>
        <div id="selected-seat-display" style="font-size: 1.4rem; font-weight: 800; color: var(--accent-cyan);">None Selected</div>
      </div>
      <div>
        <div style="font-size: 0.85rem; color: var(--text-muted);">TOTAL PRICE</div>
        <div id="total-price-display" style="font-size: 1.4rem; font-weight: 800;">$${flight.price}</div>
      </div>
      <button class="btn-book" id="btn-proceed-checkout" disabled onclick="confirmBookingCheckout()">Proceed to Checkout 💳</button>
    </div>
  `;

  renderSeatGrids();
  modal.classList.add('active');
}

function selectCabinClass(className, newPrice) {
  state.selectedClass = className;
  document.querySelectorAll('#seat-modal-body .trip-btn').forEach(btn => btn.classList.remove('active'));
  if (className === 'Economy') document.getElementById('class-econ').classList.add('active');
  if (className === 'Business') document.getElementById('class-bus').classList.add('active');
  if (className === 'First Class') document.getElementById('class-first').classList.add('active');
  
  const display = document.getElementById('total-price-display');
  if (display) display.innerText = `$${newPrice}`;
}

function renderSeatGrids() {
  const frontContainer = document.getElementById('seat-grid-front');
  const econContainer = document.getElementById('seat-grid-econ');
  if (!frontContainer || !econContainer) return;

  const rowsFront = ['1', '2', '3'];
  const cols = ['A', 'B', 'C', 'D', 'E', 'F'];
  const rowsEcon = ['4', '5', '6', '7', '8', '9'];

  const occupiedSeats = ['1B', '2D', '5A', '7F', '8C'];

  frontContainer.innerHTML = rowsFront.map(r => cols.map(c => {
    const seatId = `${r}${c}`;
    const isOcc = occupiedSeats.includes(seatId);
    return `<div class="seat-item first-class ${isOcc ? 'occupied' : ''}" onclick="selectSeat('${seatId}', ${isOcc})">${seatId}</div>`;
  }).join('')).join('');

  econContainer.innerHTML = rowsEcon.map(r => cols.map(c => {
    const seatId = `${r}${c}`;
    const isOcc = occupiedSeats.includes(seatId);
    return `<div class="seat-item ${isOcc ? 'occupied' : ''}" onclick="selectSeat('${seatId}', ${isOcc})">${seatId}</div>`;
  }).join('')).join('');
}

function selectSeat(seatId, isOccupied) {
  if (isOccupied) {
    showToast('Seat is occupied! Please choose another seat.');
    return;
  }
  state.selectedSeat = seatId;
  document.querySelectorAll('.seat-item').forEach(el => el.classList.remove('selected'));
  
  const target = Array.from(document.querySelectorAll('.seat-item')).find(el => el.innerText === seatId);
  if (target) target.classList.add('selected');

  document.getElementById('selected-seat-display').innerText = seatId;
  document.getElementById('btn-proceed-checkout').removeAttribute('disabled');
}

function confirmBookingCheckout() {
  if (!state.selectedFlight || !state.selectedSeat) return;

  const newBooking = {
    id: 'BK-' + Math.floor(1000 + Math.random() * 9000),
    flightNumber: state.selectedFlight.flightNumber,
    route: `${state.selectedFlight.origin} ✈ ${state.selectedFlight.destination}`,
    date: new Date().toISOString().split('T')[0],
    passengerName: state.activeUser.name,
    seatNumber: state.selectedSeat,
    classType: state.selectedClass,
    totalPrice: state.selectedFlight.price,
    status: 'CONFIRMED',
    ticketNumber: 'TKT-' + Math.floor(1000000 + Math.random() * 9000000)
  };

  state.bookings.unshift(newBooking);
  closeSeatModal();
  showToast(`🎉 Booking confirmed! Ticket #${newBooking.ticketNumber}`);
  switchTab('bookings');
}

function closeSeatModal() {
  const modal = document.getElementById('seat-modal');
  if (modal) modal.classList.remove('active');
}

// --- SEARCH FILTER & ADMIN ACTIONS ---
function attachSearchEvents() {
  const btn = document.getElementById('btn-execute-search');
  if (btn) {
    btn.addEventListener('click', () => {
      const orig = document.getElementById('search-origin').value.trim().toUpperCase();
      const dest = document.getElementById('search-dest').value.trim().toUpperCase();
      
      let filtered = state.flights;
      if (orig) filtered = filtered.filter(f => f.origin.includes(orig) || f.originCity.toUpperCase().includes(orig));
      if (dest) filtered = filtered.filter(f => f.destination.includes(dest) || f.destCity.toUpperCase().includes(dest));

      const container = document.getElementById('flight-results-list');
      if (container) container.innerHTML = renderFlightCards(filtered);
      showToast(`Found ${filtered.length} flight(s) matching your criteria.`);
    });
  }

  const selectAirline = document.getElementById('filter-airline');
  if (selectAirline) {
    selectAirline.addEventListener('change', (e) => {
      const val = e.target.value;
      let filtered = state.flights;
      if (val !== 'ALL') filtered = filtered.filter(f => f.airline === val);
      const container = document.getElementById('flight-results-list');
      if (container) container.innerHTML = renderFlightCards(filtered);
    });
  }
}

function deleteFlight(flightId) {
  state.flights = state.flights.filter(f => f.id !== flightId);
  showToast('Flight removed from active fleet schedule.');
  renderApp();
}

function openAddFlightModal() {
  const flightNo = prompt('Enter Flight Number (e.g. SW-990):', 'SW-990');
  if (!flightNo) return;
  const orig = prompt('Enter Origin Airport Code (e.g. JFK):', 'JFK');
  const dest = prompt('Enter Destination Airport Code (e.g. CDG):', 'CDG');
  const price = parseInt(prompt('Enter Ticket Base Price ($):', '750') || '750');

  const newF = {
    id: Date.now(),
    flightNumber: flightNo,
    airline: 'SkyWings Lux',
    origin: orig.toUpperCase(),
    originCity: 'Airport Hub',
    destination: dest.toUpperCase(),
    destCity: 'Destination City',
    depTime: '06:00 PM',
    arrTime: '09:30 AM+1',
    duration: '8h 30m',
    price: price,
    seatsAvailable: 60
  };

  state.flights.push(newF);
  showToast(`Flight ${flightNo} added successfully!`);
  renderApp();
}

function bookHotelModal(hotelName) {
  showToast(`Hotel reservation confirmed at ${hotelName}!`);
}

// --- UTILITY TOAST NOTIFICATION ---
function showToast(msg) {
  const toast = document.getElementById('toast-notification');
  if (!toast) return;
  toast.innerText = msg;
  toast.classList.add('show');
  setTimeout(() => {
    toast.classList.remove('show');
  }, 3500);
}
