# Geocoding API Guide - Get Real Restaurant Positions

## Overview

This system uses **OpenStreetMap Nominatim API** to get real coordinates for restaurants. No API key is required!

## How to Use

### Option 1: Web Interface (Easiest)

1. **Start your application:**
   ```bash
   mvn spring-boot:run
   ```

2. **Navigate to:**
   ```
   http://localhost:8080/geocode
   ```

3. **Geocode All Restaurants:**
   - Click **"🌍 Geocode All Restaurants"** button
   - Wait for the process to complete (may take several minutes)
   - All restaurant positions will be updated automatically

4. **Geocode Single Restaurant:**
   - Enter restaurant name in the input field
   - Click **"Geocode Single"** button
   - See the result immediately

5. **View Current Positions:**
   - Click **"📋 View Current Positions"** button
   - See all restaurants with their current coordinates

### Option 2: API Endpoints

#### 1. Geocode Single Restaurant
```bash
GET /api/geocode/restaurant?name=Pho24&city=Gia Lâm
```

**Response:**
```json
{
  "success": true,
  "name": "Pho24",
  "lat": 20.989700,
  "lon": 105.943500
}
```

#### 2. Geocode All Restaurants
```bash
POST /api/geocode/all-restaurants
```

**Response:**
```json
{
  "total": 38,
  "success": 25,
  "failed": 13,
  "results": [
    {
      "id": "f1",
      "name": "Pho24",
      "success": true,
      "lat": 20.989700,
      "lon": 105.943500
    },
    ...
  ]
}
```

#### 3. Get Current Positions
```bash
GET /api/geocode/current-positions
```

**Response:**
```json
{
  "count": 38,
  "restaurants": [
    {
      "id": "f1",
      "name": "Pho24",
      "lat": 20.989700,
      "lon": 105.943500
    },
    ...
  ]
}
```

#### 4. Update Single Restaurant
```bash
POST /api/geocode/update-restaurant
Content-Type: application/json

{
  "id": "f1",
  "name": "Pho24"
}
```

## How It Works

1. **API Used:** OpenStreetMap Nominatim (free, no API key)
2. **Search Query:** `RestaurantName, Gia Lâm, Hanoi, Vietnam`
3. **Rate Limiting:** 1 request per second (Nominatim requirement)
4. **Fallback:** Tries multiple search variations if first attempt fails

## Rate Limiting

- **Nominatim allows:** 1 request per second
- **Automatic delay:** 1.1 seconds between requests
- **Batch geocoding:** Takes ~1 minute per 40 restaurants

## Alternative: Google Places API

If you have a Google Places API key, you can use it instead:

1. **Set environment variable:**
   ```bash
   export GOOGLE_PLACES_API_KEY=your-api-key-here
   ```

2. **The service will automatically use Google Places** if the key is available
3. **Otherwise, it falls back to Nominatim**

## Tips for Better Results

1. **Use exact restaurant names** as they appear in OpenStreetMap
2. **Include location context:** "Pho24 Gia Lâm" works better than just "Pho24"
3. **Check results:** Some restaurants may not be found if they're not in OSM database
4. **Manual updates:** You can manually update coordinates if needed

## Troubleshooting

### Restaurant Not Found
- The restaurant may not exist in OpenStreetMap database
- Try different name variations
- Check if the restaurant is actually in Gia Lâm area

### Rate Limit Errors
- The system automatically handles rate limiting
- If you see errors, wait a few seconds and try again

### API Errors
- Check your internet connection
- Nominatim API may be temporarily unavailable
- Try again later

## Example Usage

### Using cURL

```bash
# Geocode single restaurant
curl "http://localhost:8080/api/geocode/restaurant?name=Pho24&city=Gia%20Lâm"

# Geocode all restaurants
curl -X POST http://localhost:8080/api/geocode/all-restaurants

# Get current positions
curl http://localhost:8080/api/geocode/current-positions
```

### Using JavaScript (Frontend)

```javascript
// Geocode all restaurants
async function geocodeAll() {
    const response = await fetch('/api/geocode/all-restaurants', {
        method: 'POST'
    });
    const data = await response.json();
    console.log(`Found ${data.success} restaurants`);
}
```

## Notes

- **Coordinates are updated in memory** - they persist until application restart
- **For permanent storage**, consider saving to a database or file
- **Real coordinates** may differ slightly from hardcoded values
- **Some restaurants** may not be found if they're not in OpenStreetMap

---

**Ready to use!** Just visit `/geocode` and click "Geocode All Restaurants" to get real positions for all restaurants in your system.

