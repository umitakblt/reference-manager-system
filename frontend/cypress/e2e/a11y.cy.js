/// <reference types="cypress" />
import 'cypress-axe';

describe('Erişilebilirlik Testi', () => {
  it('Anasayfa erişilebilirlik hatası vermemeli', () => {
    cy.visit('/')
    cy.injectAxe()
    cy.checkA11y()
  })

  it('Uçuş ekleme formu erişilebilirlik hatası vermemeli', () => {
    cy.visit('/flights/create')
    cy.injectAxe()
    cy.checkA11y()
  })
}) 