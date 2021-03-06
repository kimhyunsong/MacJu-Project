import { useEffect, useState } from "react";
import StarRate from './StartRate.js'
import Modal from 'react-modal';
import '../../styles/BeerRate.css'
import axiosInstance from "CustomAxios.js";
import { useStore } from "react-redux";

function BeerRate(props){
  const BEER_RATE_URL = process.env.REACT_APP_SERVER + ':8888/v1/beer'
  const [flavorArr, setFlavorArr] = useState([])     
  const [flavorIdArr, setFlavorIdArr] = useState([])  
  const [aromaArr, setAromaArr] = useState([])      
  const [aromaIdArr, setAromaIdArr] = useState([])  
  const store = useStore((state)=>state)
  const memberId = store.getState().userReducer.memberId 
  const starrate = props.starrate
  const beerid = props.beerid


  
  
  const modal_style = {
    overlay: {
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      backgroundColor: 'rgba(52, 52, 52, 0.9)'
    },
    content: {
      position: 'absolute',
      width: '300px',
      height: '500px',
      top: '200px',
      bottom: '200px',
      margin: 'auto',
      border: '1px solid #ccc',
      background: '#fff',
      overflow: 'auto',
      WebkitOverflowScrolling: 'touch',
      borderRadius: '4px',
      outline: 'none',
    }
  }

  const addflavor = ((e)=>{
    const nowtag = e.target.innerText.substring(1)
    const nowid = e.target.value
    if (flavorArr.indexOf(nowtag) === -1) {
      setFlavorArr((flavorArr) => [...flavorArr, nowtag])
      setFlavorIdArr((flavorid) => [...flavorid, nowid])
    }
  })
  const deleteFlavor = (e) => {
    const nowtag = e.target.outerText.substring(1)
    setFlavorArr(flavorArr.filter((flavor) => flavor !== nowtag ))
  }


  const addAroma = ((e)=>{
    const nowtag = e.target.innerText.substring(1)
    const nowid = e.target.value
    if (aromaArr.indexOf(nowtag) === -1) {
      setAromaArr((aromaArr) => [...aromaArr, nowtag])
      setAromaIdArr((aromaid) => [...aromaid, nowid])
    }
  })
  const deleteAroma = (e) => {
    const nowtag = e.target.outerText.substring(1)
    setAromaArr(aromaArr.filter((aroma) => aroma !== nowtag ))
  }
  const updateRate = async ()=>{
    const {data:ratedata} = await axiosInstance.get(`${BEER_RATE_URL}/${beerid}/member/${memberId}`)
    props.setStarrate(ratedata.rate)
  }
  const submitRate = ()=> {
    const newrate = {
      aromaHashTags : aromaIdArr,
      flavorHashTags : flavorIdArr,
      rate : starrate
    }
    const headers = {
      'Content-Type': 'application/json; charset=UTF-8',
      'Accept': "application/json; charset=UTF-8"
    }
    if (aromaArr.length && flavorArr.length && starrate) {
      axiosInstance.put(`${BEER_RATE_URL}/${beerid}/member/${memberId}`, newrate, {headers}) 
      .then(() => {
        props.set_rateModal(false)
        updateRate();
      })
    } else {
      alert('????????? ??????????????????!')
    }
  }

  useEffect(()=>{
    const fetchData = async ()=>{
      const {data:ratedata} = await axiosInstance.get(`${BEER_RATE_URL}/${beerid}/member/${memberId}`)
      props.setStarrate(ratedata.rate)

      ratedata.aromaHashTags.map((tag)=>{
        if (aromaArr.indexOf(tag.aroma) === -1) {
          setAromaArr((arr)=>[...arr, tag.aroma])
          setAromaIdArr((arr)=>[...arr, tag.id])
        }
      })
      ratedata.flavorHashTags.map((tag)=>{
        if (flavorArr.indexOf(tag.flavor) === -1) {
          setFlavorArr((arr)=>[...arr, tag.flavor])
          setFlavorIdArr((arr)=>[...arr, tag.id])
        }
      })
    }
    fetchData();
  }, [])



  return(
    <Modal isOpen={props.rateModal} style={modal_style} ariaHideApp={false} >
    <div className="ratemodal_section">
      <h4 className="modal_heading">Beer Rate</h4>
      <StarRate setStarrate={props.setStarrate} starrate={props.starrate}></StarRate>
      <div className="row"> 
        <div className="selecttag_box col-6"> 
          <h4>Flavor</h4>
          <select className="rate_select"  multiple>
            <option value="" disabled>
              ??? ??????!
            </option>
            <option onClick={addflavor} value="1" >#??????</option>
            <option onClick={addflavor} value="2" >#??????</option>
            <option onClick={addflavor} value="3" >#??????</option>
            <option onClick={addflavor} value="4" >#?????????</option>
            <option onClick={addflavor} value="5" >#?????????</option>
            <option onClick={addflavor} value="6" >#????????????</option>
            <option onClick={addflavor} value="7" >#????????????</option>
            <option onClick={addflavor} value="8" >#????????????</option>
            <option onClick={addflavor} value="9" >#????????????</option>
            <option onClick={addflavor} value="10">#????????????</option>
            <option onClick={addflavor} value="11">#????????????</option>
            <option onClick={addflavor} value="12">#????????????</option>
            <option onClick={addflavor} value="13">#????????????</option>
          </select>
          <div className="flavortag_div">
            { flavorArr && flavorArr.map((flavor, i)=>{
              return (
                <div key={i} className="flavor_wrap_inner" onClick={deleteFlavor}>#{flavor}</div>
              )
            }) }
          </div>
        </div>
        <div className="selecttag_box col-6"> 
          <h4>Aroma</h4>
          <div></div>
          <select className="rate_select"  multiple>
            <option value="" disabled>
              ??? ??????!
            </option>
            <option onClick={addAroma} value="1">#??????</option>
            <option onClick={addAroma} value="2">#??????</option>
            <option onClick={addAroma} value="3">#????????????</option>
            <option onClick={addAroma} value="4">#?????????</option>
            <option onClick={addAroma} value="5">#?????????</option>
            <option onClick={addAroma} value="6">#????????????</option>
            <option onClick={addAroma} value="7">#????????????</option>
            <option onClick={addAroma} value="8">#????????????</option>
            <option onClick={addAroma} value="9">#????????????</option>
            <option onClick={addAroma} value="10">#????????????</option>
            <option onClick={addAroma} value="11">#????????????</option>
            <option onClick={addAroma} value="12">#??????</option>
            <option onClick={addAroma} value="13">#????????????</option>
            <option onClick={addAroma} value="14">#?????????</option>
            <option onClick={addAroma} value="15">#?????????</option>
            <option onClick={addAroma} value="16">#??????</option>
            <option onClick={addAroma} value="17">#?????????</option>
            <option onClick={addAroma} value="18">#?????????</option>
            <option onClick={addAroma} value="19">#?????????</option>
            <option onClick={addAroma} value="20">#????????????</option>
            <option onClick={addAroma} value="21">#????????????</option>
            <option onClick={addAroma} value="22">#?????????</option>
            <option onClick={addAroma} value="23">#?????????</option>
            <option onClick={addAroma} value="24">#????????????</option>
            <option onClick={addAroma} value="25">#?????????</option>
            <option onClick={addAroma} value="26">#??????</option>
            <option onClick={addAroma} value="27">#?????????</option>
            <option onClick={addAroma} value="28">#????????????</option>
            <option onClick={addAroma} value="29">#????????? </option>
          </select>
          <div className="aromatag_div">
            {aromaArr && aromaArr.map((aroma, i)=>{
              return (
                <div key={i} className="aroma_wrap_inner" onClick={deleteAroma}>#{aroma}</div>
              )
            })}
          </div>
        </div>
      </div>
      <button className="submitRateBtn" onClick={submitRate}>??????</button>
    </div>
  </Modal>
  )
}

export default BeerRate;
