import { mount } from '@vue/test-utils'
import { describe, it, expect } from 'vitest'
import FlightCreate from './FlightCreate.vue'

describe('FlightCreate', () => {
  it('form alanları render edilir', () => {
    const wrapper = mount(FlightCreate)
    expect(wrapper.find('input#flightNumber').exists()).toBe(true)
    expect(wrapper.find('input#origin').exists()).toBe(true)
    expect(wrapper.find('input#destination').exists()).toBe(true)
  })

  it('hatalı uçuş numarası girilirse hata mesajı gösterir', async () => {
    const wrapper = mount(FlightCreate)
    await wrapper.find('input#flightNumber').setValue('1234')
    await wrapper.find('form').trigger('submit.prevent')
    expect(wrapper.html()).toContain('Flight Number formatı hatalı')
  })
}) 