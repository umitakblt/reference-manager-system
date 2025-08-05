export function getAirlineName(id, airlines) {
  if (!Array.isArray(airlines)) return id
  const airline = airlines.find(a => a.id === id || a.id === String(id) || a.id === Number(id))
  return airline ? airline.name : id
}

export function getAircraftName(id, aircrafts) {
  if (!Array.isArray(aircrafts)) {
    console.log('❌ Aircrafts array değil:', aircrafts)
    return id
  }
  const aircraft = aircrafts.find(a => a.id === id || a.id === String(id) || a.id === Number(id))
  console.log(`🔍 Aircraft arama - ID: ${id}, Bulunan:`, aircraft)
  return aircraft ? aircraft.name : id
}

export function getStationName(id, stations) {
  if (!Array.isArray(stations)) return id
  const station = stations.find(s => s.id === id || s.id === String(id) || s.id === Number(id))
  return station ? station.name : id
}