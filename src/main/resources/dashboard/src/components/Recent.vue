<style>
</style>

<template>
<div id="app">
    <div class="container margin-md">
      <h1 class="header">Recent Meter Readings</h1>
    </div>
    <div class="container">
      <table class="table">
        <thead>
          <tr>
            <td scope="col">Timestamp</td>
            <td scope="col">Site ID</td>
            <td scope="col">Watt-hours Generated</td>
            <td scope="col">Watt-hours Used</td>
            <td scope="col">Temp (Celsius)</td>
          </tr>
        </thead>
        <tr v-for="reading in meterReadings" :key="reading.siteId">
          <td>{{ new Date(reading.dateTime * 1000).toISOString() }}</td>
          <td><router-link :to="{ name: 'stats', params: { id: reading.siteId }}">{{ reading.siteId }}</router-link></td>
          <td>{{ Number.parseFloat(reading.whGenerated).toPrecision(4) }}</td>
          <td>{{ Number.parseFloat(reading.whUsed).toPrecision(4) }}</td>
          <td>{{ Number.parseFloat(reading.tempC).toPrecision(4) }}</td>
        </tr>
      </table>
    </div>
  </div>
</template>

<script>
</script>

<style>
</style>

<script>

import axios from 'axios'
export default {
  name: 'Recent',
  data: function () {
    return {
      meterReadings: [],
    }
  },
  mounted () {
    this.getData()
  },
  methods: {
    getData () {
      axios.get(`${process.env.apiHost}api/meterReadings/`)
        .then((response) => {
          this.meterReadings = response.data
        })
        .catch(function (error) {
          console.log('Got error')
          console.log(error)
        })
      }
    }
}

</script>
