/**
  ******************************************************************************
  * @file    sensor_service.h
  * @author  CL
  * @version V1.0.0
  * @date    04-July-2014
  * @brief
  ******************************************************************************
  * @attention
  *
  * <h2><center>&copy; COPYRIGHT(c) 2014 STMicroelectronics</center></h2>
  *
  * Redistribution and use in source and binary forms, with or without modification,
  * are permitted provided that the following conditions are met:
  *   1. Redistributions of source code must retain the above copyright notice,
  *      this list of conditions and the following disclaimer.
  *   2. Redistributions in binary form must reproduce the above copyright notice,
  *      this list of conditions and the following disclaimer in the documentation
  *      and/or other materials provided with the distribution.
  *   3. Neither the name of STMicroelectronics nor the names of its contributors
  *      may be used to endorse or promote products derived from this software
  *      without specific prior written permission.
  *
  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
  * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
  * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
  * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
  * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
  * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
  * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
  * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  *
  ******************************************************************************
  */

/* Define to prevent recursive inclusion -------------------------------------*/
#ifndef _SENSOR_SERVICE_H_
#define _SENSOR_SERVICE_H_

/* Includes ------------------------------------------------------------------*/
#include "hal_types.h"
#include "bluenrg_gatt_server.h"
#include "bluenrg_gap.h"
#include "string.h"
#include "bluenrg_gap_aci.h"
#include "bluenrg_gatt_aci.h"
#include "hci_const.h"
#include "gp_timer.h"
#include "bluenrg_hal_aci.h"
#include "bluenrg_aci_const.h"
#include "hci.h"
#include "hci_le.h"
#include "hal.h"
#include "sm.h"
#include "debug.h"
#include <SPBTLE_RF.h>
#include <stdlib.h>

/** @addtogroup X-CUBE-BLE1_Applications
 *  @{
 */

/** @addtogroup SensorDemo
 *  @{
 */

/** @addtogroup SENSOR_SERVICE
 * @{
 */

/** @addtogroup SENSOR_SERVICE_Exported_Defines
 * @{
 */
/* Exported defines ----------------------------------------------------------*/

/**
 * @}
 */

/** @addtogroup SENSOR_SERVICE_Exported_Types
 *  @{
 */
typedef int i32_t;

/**
 * @brief Heartbeat structure
 */
typedef struct {
  i32_t ERR;
  i32_t meanOfLast3;
  i32_t lastIBI;
  i32_t secondToLastIBI;
  i32_t thirdToLastIBI;
  i32_t meanOfLast10;
} Heartbeat;
/**
 * @}
 */

/** @addtogroup SENSOR_SERVICE_Exported_Functions
 *  @{
 */
class SensorServiceClass
{
  public:
    tBleStatus begin(const char *name, uint8_t addr[BDADDR_SIZE]);

    void       setConnectable(void);
    int        isConnected(void);

    tBleStatus Add_Heartbeat_Service(void);
    tBleStatus Heartbeat_Notify(Heartbeat *data);

    void       GAP_ConnectionComplete_CB(uint8_t addr[BDADDR_SIZE], uint16_t handle);
    void       GAP_DisconnectionComplete_CB(void);
    void       Read_Request_CB(uint16_t handle);
    void       Attribute_Modified_CB(uint16_t handle, uint8_t data_length,
                                     uint8_t *att_data);

    uint16_t heartServHandle, bpmCharHandle;

  private:
    volatile uint16_t connection_handle = 0;

    volatile bool connected = FALSE;
    volatile bool set_connectable = TRUE;
    volatile bool notification_enabled = FALSE;

    volatile Heartbeat heartbeat_data;

    bool ledState = false;
    uint32_t previousMinuteValue = 0;
};

extern SensorServiceClass SensorService;

/**
 * @}
 */

/**
 * @}
 */

/**
 * @}
 */

/**
 * @}
 */

#endif /* _SENSOR_SERVICE_H_ */

/************************ (C) COPYRIGHT STMicroelectronics *****END OF FILE****/
