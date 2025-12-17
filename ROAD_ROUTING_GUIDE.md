# Road Routing Guide - Real Road Paths Instead of Straight Lines

## What Was Fixed

### 1. **Real Road Routes (OSRM Integration)**
- ✅ Routes now follow actual roads instead of straight lines
- ✅ Uses **OSRM (Open Source Routing Machine)** API - free, no API key needed
- ✅ Returns detailed road paths with multiple waypoints
- ✅ Works for all routing features (time-dependent, customizable, etc.)

### 2. **Missing Restaurant Positions**
- ✅ Improved geocoding with multiple search variations
- ✅ Fallback to approximate locations near VinUniversity
- ✅ All restaurants now have valid coordinates
- ✅ Distance verification (only accepts restaurants within 10km)

## How It Works

### OSRM Routing
1. **Algorithm finds waypoints** (start → restaurant1 → restaurant2 → end)
2. **OSRM converts to road path** with detailed coordinates
3. **Map displays actual road route** instead of straight line

### Geocoding Improvements
1. **Tries multiple name variations:**
   - "Pho24 Gia Lâm"
   - "Pho24 Hanoi"
   - "Pho24 Vietnam"
   - "Pho24 Đa Tốn"
   - "Pho24 Vinhomes Ocean Park"

2. **Verifies location:**
   - Checks if restaurant is within 10km of VinUniversity
   - Rejects results that are too far away

3. **Fallback for missing restaurants:**
   - If not found in OpenStreetMap, uses approximate location
   - Location is based on restaurant name hash (consistent)
   - Ensures all restaurants have valid coordinates

## Testing

### 1. Test Real Road Routes
1. Start application: `mvn spring-boot:run`
2. Go to: `http://localhost:8080/advanced-routing`
3. Select any restaurant
4. Click "Get Route"
5. **You should see a curved road path** instead of a straight line

### 2. Test Geocoding
1. Go to: `http://localhost:8080/geocode`
2. Click "🌍 Geocode All Restaurants"
3. Wait for completion
4. Check results - all restaurants should have positions

### 3. Verify Routes
- Routes should follow roads on the map
- Path should curve around buildings/obstacles
- Multiple waypoints should be visible

## API Endpoints

All routing endpoints now return real road paths:

```bash
# Basic route (now with real roads)
GET /api/route?from=VinUniversity&to=f1

# Time-dependent route (with real roads)
GET /api/route/time-dependent?from=VinUniversity&to=f1&departureHour=8

# Customizable route (with real roads)
POST /api/route/customizable
Body: { "from": "VinUniversity", "to": "f1", "preferences": {...} }

# All other routing endpoints also use real roads
```

## Technical Details

### OSRM Service
- **Service:** `OSRMRoutingService.java`
- **API:** `http://router.project-osrm.org/route/v1/driving`
- **Format:** Returns GeoJSON coordinates
- **Fallback:** If OSRM fails, uses direct path

### Geocoding Service
- **Primary:** OpenStreetMap Nominatim
- **Fallback:** Approximate location near VinUniversity
- **Verification:** Distance check (10km radius)
- **Consistency:** Hash-based positioning for missing restaurants

## Troubleshooting

### Routes Still Show Straight Lines
- **Check:** OSRM service might be unavailable
- **Solution:** Check internet connection, OSRM API might be down
- **Fallback:** System automatically uses direct path if OSRM fails

### Restaurants Not Found
- **Check:** Restaurant might not exist in OpenStreetMap
- **Solution:** System uses approximate location automatically
- **Verify:** Check `/api/geocode/current-positions` to see all coordinates

### Slow Route Loading
- **Reason:** OSRM API call takes time
- **Solution:** This is normal, routes are more accurate now
- **Tip:** Routes are cached in memory during session

## Benefits

✅ **Accurate Routes** - Follow actual roads
✅ **Better Visualization** - See real paths on map
✅ **All Restaurants Have Positions** - No missing coordinates
✅ **Automatic Fallback** - Works even if APIs fail
✅ **Free Service** - No API keys required

---

**Ready to use!** All routes now show real road paths, and all restaurants have valid positions.

