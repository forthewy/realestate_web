import { useState } from 'react'
import Sidebar from './components/layout/Sidebar'
import Dashboard from './pages/Dashboard'
import Header from './components/layout/Header'
import Router from "./router/Router";

function App() {
  return (
    <>
      <Router />
      <Header />
      <Sidebar />
      
      <Dashboard />
    </>
  )
}

export default App
